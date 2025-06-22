package ToStart;

import Classes.RouteDTO;
import InputHandler.JsonToRouteMapper;
import InputHandler.RouteInputDialog;
import InputHandler.ScriptInputDialog;
import View.InfoTabContent;
import View.Localization;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import View.MyBoundingBox;
import javafx.scene.control.TabPane;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static View.Localization.getCurrentZone;

public class MainWindowController {
    private final BorderPane root = new BorderPane();
    private Tab mainTab;
    private Tab infoTab;
    private final TabPane tabPane = new TabPane();
    private final ObservableList<RouteDTO> data = FXCollections.observableArrayList();
    private final TableView<RouteDTO> tableView = new TableView<>(data);
    private final String currentUser;
    private Map<String, RouteDTO> routeMap = new LinkedHashMap<>();
    private Canvas canvas;
    private Map<String, Paint> userColors = new HashMap<>();
    private final Random random = new Random();
    private final Gson gson;
    private final ClientNetworkManager clientNetworkManager;
    Consumer<String> sendMessage;
    private boolean initialLoadDone = false;
    private final ObjectProperty<Map<String, RouteDTO>> routeMapProperty = new SimpleObjectProperty<>();
    private final Map<TableColumn<RouteDTO, ?>, String> originalColumnTitles = new HashMap<>();
    private final Map<TableColumn<RouteDTO, ?>, String> originalSubColumnTitles = new HashMap<>();
    public ObjectProperty<Map<String, RouteDTO>> routeMapProperty() {
        return routeMapProperty;
    }

    public MainWindowController(ClientNetworkManager clientNetworkManager, String username) {
        this.clientNetworkManager = clientNetworkManager;
        this.sendMessage = clientNetworkManager.getSendMessage();
        this.gson = clientNetworkManager.getGson();
        this.currentUser = username;
        this.canvas = new Canvas(300, 300); // Возвращаем исходный размер
        StackPane canvasContainer = new StackPane(canvas);
        canvasContainer.setStyle("-fx-background-color: lightgray;");
        canvasContainer.setMinSize(300, 300);
        canvasContainer.setMaxSize(300, 300);
        Localization.setLocale(new Locale("ru"));
        setupTable();
        Label userLabel = new Label(Localization.getString("user") + username);
        Button scriptButton = new Button(Localization.getString("script"));
        scriptButton.setOnAction(event -> {
            ScriptInputDialog dialog = new ScriptInputDialog(username, gson, sendMessage);
            dialog.showAndSend();

            // Подписываемся на ответ от сервера
            ChangeListener<CommandResponse> listener = new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends CommandResponse> obs, CommandResponse oldVal, CommandResponse newVal) {
                    if (newVal != null && newVal.isSuccess()) {
                        // Только после успешного выполнения add — запрашиваем обновление данных
                        clientNetworkManager.sendGetRoutesCommand();
                        clientNetworkManager.loadRoutesFromMapAsync();

                    }
                    // Отписываемся после первого срабатывания
                    clientNetworkManager.commandResponseProperty().removeListener(this);
                }
            };

            // Подписываем слушатель
            clientNetworkManager.commandResponseProperty().addListener(listener);
            clientNetworkManager.loadRoutesFromMapAsync();
        });

        Button addButton = new Button("Добавить");
        addButton.setOnAction(event -> {
            RouteInputDialog dialog = new RouteInputDialog(username, gson, sendMessage);
            dialog.showAndSendAdd(); // отправляем add-команду

            // Подписываемся на ответ от сервера
            ChangeListener<CommandResponse> listener = new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends CommandResponse> obs, CommandResponse oldVal, CommandResponse newVal) {
                    if (newVal != null && newVal.isSuccess()) {
                        // Только после успешного выполнения add — запрашиваем обновление данных
                        clientNetworkManager.sendGetRoutesCommand();
                        clientNetworkManager.loadRoutesFromMapAsync();

                    }
                    // Отписываемся после первого срабатывания
                    clientNetworkManager.commandResponseProperty().removeListener(this);
                }
            };

            // Подписываем слушатель
            clientNetworkManager.commandResponseProperty().addListener(listener);
            clientNetworkManager.loadRoutesFromMapAsync();
        });
        Button removeButton = new Button("Удалить");
        removeButton.setOnAction(event -> {
            RouteDTO selectedRoute = tableView.getSelectionModel().getSelectedItem();
            if (selectedRoute == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ничего не выбрано");
                alert.setHeaderText(null);
                alert.setContentText("Выберите маршрут для удаления.");
                alert.showAndWait();
                return;
            }

            if (!selectedRoute.getOwner().equals(username)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка доступа");
                alert.setHeaderText(null);
                alert.setContentText("Вы не можете удалить маршрут, который вам не принадлежит.");
                alert.showAndWait();
                return;
            }

            if (selectedRoute != null) {
                tableView.getItems().remove(selectedRoute);
                clientNetworkManager.sendCommand("remove_by_key", selectedRoute.getKey(), username);
                new Thread(() -> {
                    try {
                        Thread.sleep(1000); // Даем серверу время обработать
                        Platform.runLater(() -> clientNetworkManager.sendGetRoutesCommand());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        });
        Button clearCollectionButton = new Button("Очистить коллекцию");
        clearCollectionButton.setOnAction(event -> {
            // Получаем список всех маршрутов пользователя
            List<RouteDTO> userRoutes = tableView.getItems().stream()
                    .filter(route -> route.getOwner().equals(username))
                    .collect(Collectors.toList());

            if (userRoutes.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Нет маршрутов");
                alert.setHeaderText(null);
                alert.setContentText("У вас нет маршрутов для удаления.");
                alert.showAndWait();
                return;
            }

            // Подтверждение удаления
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Подтверждение удаления");
            confirmation.setHeaderText("Вы уверены, что хотите удалить все свои маршруты?");
            confirmation.setContentText("Будет удалено " + userRoutes.size() + " маршрутов.");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Удаляем маршруты из таблицы
                tableView.getItems().removeAll(userRoutes);

                // Отправляем команды на сервер для каждого маршрута
                new Thread(() -> {
                    for (RouteDTO route : userRoutes) {
                        clientNetworkManager.sendCommand("remove_by_key", route.getKey(), username);
                    }

                    // Даем серверу время обработать и обновляем данные
                    try {
                        Thread.sleep(1000);
                        Platform.runLater(() -> clientNetworkManager.sendGetRoutesCommand());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
        Button refreshButton = new Button("Обновить");
        refreshButton.setOnAction(event -> clientNetworkManager.loadRoutesFromMapAsync());
        Button editButton = new Button("Редактировать");
        editButton.setOnAction(event -> {
            RouteDTO selectedRoute = tableView.getSelectionModel().getSelectedItem();
            if (selectedRoute != null) {
                if (!selectedRoute.getOwner().equals(username)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText(null);
                    alert.setContentText("Вы не можете редактировать чужие маршруты.");
                    alert.showAndWait();
                    return;
                }
                // Открываем диалог редактирования
                RouteInputDialog dialog = new RouteInputDialog(username, gson, sendMessage);
                dialog.setRouteData(selectedRoute); // Предзаполняем поля
                dialog.showAndSendUpdate(selectedRoute);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ничего не выбрано");
                alert.setHeaderText(null);
                alert.setContentText("Выберите маршрут для редактирования.");
                alert.showAndWait();
            }
            // Подписываемся на ответ от сервера
            ChangeListener<CommandResponse> listener = new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends CommandResponse> obs, CommandResponse oldVal, CommandResponse newVal) {
                    if (newVal != null && newVal.isSuccess()) {
                        // Только после успешного выполнения add — запрашиваем обновление данных
                        clientNetworkManager.sendGetRoutesCommand();
                        clientNetworkManager.loadRoutesFromMapAsync();
                    }
                    // Отписываемся после первого срабатывания
                    clientNetworkManager.commandResponseProperty().removeListener(this);
                }
            };

            // Подписываем слушатель
            clientNetworkManager.commandResponseProperty().addListener(listener);
            clientNetworkManager.loadRoutesFromMapAsync();

        });

        HBox buttonBox = new HBox(10, addButton, removeButton, editButton, scriptButton, clearCollectionButton, refreshButton);


        // Устанавливаем минимальную ширину таблицы и максимальную для растяжения
        tableView.setMinWidth(400);
        tableView.setMaxWidth(Double.MAX_VALUE);

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            for (RouteDTO route : routeMap.values()) {
                if (route.getBoundingBox() != null && route.getBoundingBox().contains(mouseX, mouseY)) {
                    showRouteInfo(route);
                    break;
                }
            }
        });

        // Создаем первую вкладку с таблицей и картой
        this.mainTab = new Tab(Localization.getString("routes"));
        mainTab.setClosable(false); // Запрещаем закрывать вкладку

        HBox hBox = new HBox(10); // 10 — отступ между элементами
        hBox.getChildren().addAll(tableView, canvasContainer);
        HBox.setHgrow(tableView, Priority.ALWAYS);
        HBox.setHgrow(canvasContainer, Priority.NEVER);
        BorderPane mainContent = new BorderPane();
        mainContent.setCenter(hBox);
        mainContent.setBottom(buttonBox);
        mainContent.setPrefSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE);
        mainTab.setContent(mainContent);

        InfoTabContent infoTabContent = new InfoTabContent(routeMapProperty());
        this.infoTab = infoTabContent.getTab();

        tabPane.getTabs().addAll(mainTab, infoTab);
        ChoiceBox<Locale> languageSelector = new ChoiceBox<>();
        languageSelector.getItems().addAll(Localization.getSupportedLocales());

        // Устанавливаем текущую локаль
        languageSelector.setValue(Locale.getDefault());

        languageSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("Language selector изменился: " + newVal);
                Localization.setLocale(newVal);
                updateUILanguage(userLabel, addButton, editButton,removeButton, scriptButton, clearCollectionButton, refreshButton); // Обновляем элементы интерфейса

            }
        });
        // Устанавливаем TabPane как основное содержимое root
        HBox topPanel = new HBox(10, userLabel, languageSelector);
        topPanel.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        root.setTop(topPanel);
        root.setCenter(tabPane);

        clientNetworkManager.routeResponseProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String jsonArgs = newVal.getMessage();
                if (jsonArgs != null && jsonArgs.trim().startsWith("{")) {
                    try {
                        routeMap = JsonToRouteMapper.parseJsonToRouteMap();
                        Platform.runLater(this::updateTableAndCanvas);
                        System.out.println("Коллекция обновилась — обновляем UI");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Подписка на commandResponse (для remove_by_key и других команд)
        clientNetworkManager.commandResponseProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isSuccess() && (!initialLoadDone)) {
                // ВСЕГДА запрашиваем актуальные данные после успешной команды
                clientNetworkManager.sendGetRoutesCommand();
                clientNetworkManager.loadRoutesFromMapAsync();
            }
        });
        clientNetworkManager.routeResponseProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String jsonArgs = newVal.getMessage();
                if (jsonArgs != null && jsonArgs.trim().startsWith("{")) {
                    try {
                        routeMap = JsonToRouteMapper.parseJsonToRouteMap();
                        Platform.runLater(() -> {
                            updateTableAndCanvas();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if (!initialLoadDone) {
            initialLoadDone = true;
            System.out.println("Первоначальная загрузка маршрутов...");
            clientNetworkManager.loadRoutesFromMapAsync();
        }

        // Рисуем простой фон на canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvasContainer.setStyle("-fx-background-color: lightgray; -fx-border-color: #0024b6; -fx-border-width: 2px;");
    }



    private void updateUILanguage(Label userLabel, Button addButton, Button editButton, Button removeButton, Button scriptButton, Button clearCollectionButton, Button refreshButton) {
        // Обновление текстовых меток
        userLabel.setText(Localization.getString("user_label") + currentUser);
        addButton.setText(Localization.getString("add"));
        removeButton.setText(Localization.getString("remove"));
        editButton.setText(Localization.getString("edit"));
        scriptButton.setText(Localization.getString("execute_script"));
        clearCollectionButton.setText(Localization.getString("clear_collection"));
        refreshButton.setText(Localization.getString("refresh"));
        // Обновляем заголовки вкладок
        mainTab.setText(Localization.getString("routes"));
        infoTab.setText(Localization.getString("information"));

        // Обновление заголовков таблицы
        for (TableColumn<RouteDTO, ?> col : tableView.getColumns()) {
            String originalTitle = originalColumnTitles.get(col);
            if (originalTitle != null) {
                switch (originalTitle) {
                    case "ID": col.setText(Localization.getString("id")); break;
                    case "Название": col.setText(Localization.getString("name")); break;
                    case "X": col.setText(Localization.getString("x")); break;
                    case "Y": col.setText(Localization.getString("y")); break;
                    case "Владелец": col.setText(Localization.getString("owner")); break;
                    case "Дата создания": col.setText(Localization.getString("creation_date")); break;
                    case "Откуда": col.setText(Localization.getString("from")); break;
                    case "Куда": col.setText(Localization.getString("to")); break;
                    case "Ключ": col.setText(Localization.getString("key")); break;
                }
            }

            // Обновляем подколонки
            for (TableColumn<RouteDTO, ?> subCol : col.getColumns()) {
                String originalSubTitle = originalSubColumnTitles.get(subCol);
                if (originalSubTitle != null) {
                    switch (originalSubTitle) {
                        case "Откуда":
                            subCol.setText(Localization.getString("from_name"));
                            break;
                        case "Куда":
                            subCol.setText(Localization.getString("to_name"));
                            break;
                        case "X": subCol.setText("X"); break;
                        case "Y": subCol.setText("Y"); break;
                        case "Z": subCol.setText("Z"); break;
                    }
                }
            }
        }
        // Полное обновление данных таблицы
        ObservableList<RouteDTO> items = tableView.getItems();
        tableView.setItems(FXCollections.observableArrayList());
        tableView.setItems(items);
        updateTabTitles();
        drawRoutes(); // Перерисовываем легенду
    }
    private void updateTabTitles() {
        mainTab.setText(Localization.getString("routes"));
        infoTab.setText(Localization.getString("information"));
    }
    public BorderPane getView() {
        return root;
    }
    private void updateTableAndCanvas() {
        ObservableList<RouteDTO> routeList = FXCollections.observableArrayList(routeMap.values());
        tableView.setItems(routeList);
        drawRoutes();
        this.routeMapProperty.set(routeMap); // Уведомляем слушателей
    }
    private void setupTable() {
        // Колонка ID
        TableColumn<RouteDTO, Integer> idCol = new TableColumn<>(Localization.getString("id"));
        originalColumnTitles.put(idCol, "ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Колонка Название
        TableColumn<RouteDTO, String> nameCol = new TableColumn<>(Localization.getString("name"));
        originalColumnTitles.put(nameCol, "Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Колонка X
        TableColumn<RouteDTO, Number> xCol = new TableColumn<>(Localization.getString("x"));
        originalColumnTitles.put(xCol, "X");
        xCol.setCellValueFactory(new PropertyValueFactory<>("x"));

        // Колонка Y
        TableColumn<RouteDTO, Number> yCol = new TableColumn<>(Localization.getString("y"));
        originalColumnTitles.put(yCol, "Y");
        yCol.setCellValueFactory(new PropertyValueFactory<>("y"));

        // Колонка Владелец
        TableColumn<RouteDTO, String> ownerCol = new TableColumn<>(Localization.getString("owner"));
        originalColumnTitles.put(ownerCol, "Владелец");
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));

        // Колонка Дата создания
        TableColumn<RouteDTO, String> dateCol = new TableColumn<>(Localization.getString("creation_date"));
        originalColumnTitles.put(dateCol, "Дата создания");
        // Создаем динамически обновляемый формат даты
        dateCol.setCellValueFactory(data -> {
            RouteDTO route = data.getValue();
            long timestamp = route.getCreationDate();
            ZoneId userZone = getCurrentZone();
            String formattedDate = Localization.formatDate(timestamp, userZone);
            return new SimpleStringProperty(formattedDate);
        });

        // Добавляем слушатель изменения локали
        Localization.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            // Принудительно обновляем все ячейки
            Platform.runLater(() -> {
               tableView.refresh();
               clientNetworkManager.loadRoutesFromMapAsync();

            });
        });


        // Колонка From
        TableColumn<RouteDTO, String> fromCol = new TableColumn<>(Localization.getString("from"));
        originalColumnTitles.put(fromCol, "Откуда");
        fromCol.getColumns().addAll(
                createSubColumn(Localization.getString("from_name"), "fromName"),
                createSubColumn("X", "fromX"),
                createSubColumn("Y", "fromY"),
                createSubColumn("Z", "fromZ")
        );

        // Колонка To
        TableColumn<RouteDTO, String> toCol = new TableColumn<>(Localization.getString("to"));
        originalColumnTitles.put(toCol, "Куда");
        toCol.getColumns().addAll(
                createSubColumn(Localization.getString("to_name"), "toName"),
                createSubColumn("X", "toX"),
                createSubColumn("Y", "toY"),
                createSubColumn("Z", "toZ")
        );

        // Колонка Ключ
        TableColumn<RouteDTO, String> keyCol = new TableColumn<>(Localization.getString("key"));
        originalColumnTitles.put(keyCol, "Ключ");
        keyCol.setCellValueFactory(new PropertyValueFactory<>("key"));

        // Добавляем все колонки
        tableView.getColumns().addAll(idCol, nameCol, xCol, yCol, ownerCol, dateCol, fromCol, toCol, keyCol);

        // Устанавливаем пустой список на старте
        tableView.setItems(FXCollections.observableArrayList());
    }
    private TableColumn<RouteDTO, String> createSubColumn(String title, String field) {
        TableColumn<RouteDTO, String> col = new TableColumn<>(title);
        originalSubColumnTitles.put(col, title);
        col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getByKey(field)));
        return col;
    }

    private void drawRoutes() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double scale = 10;
        userColors.clear();
        double offsetY = canvas.getHeight(); // Начало Y — снизу
        for (RouteDTO route : routeMap.values()) {
            double x = route.getX() * scale;
            double y = offsetY - route.getY() * scale;
            String owner = route.getOwner();

            Paint color = getColorForUser(owner); // замена на стабильный цвет

            // Рисуем точку
            gc.setFill(color);
            gc.fillOval(x - 5, y - 5, 10, 10);

            // Текст с ID
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(route.getId()), x + 8, y + 4);

            // Сохраняем область клика
            route.setBoundingBox(new MyBoundingBox(x, y, 10, 10, route));
        }
        drawLegend(gc, userColors); // передаём одну и ту же карту
    }
    private void showRouteInfo(RouteDTO route) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация о маршруте");
        alert.setHeaderText(null);
        alert.setContentText(
                "ID: " + route.getId() + "\n" +
                        "Название: " + route.getName() + "\n" +
                        "Владелец: " + route.getOwner() + "\n" +
                        "From: " + route.getFromName() + " (" + route.getFromX() + ", " + route.getFromY() + ", " + route.getFromZ() + ")" + "\n" +
                        "To: " + route.getToName() + " (" + route.getToX() + ", " + route.getToY() + ", " + route.getToZ() + ")"
        );
        alert.showAndWait();
    }



    private Paint getColorForUser(String owner) {
        if (!userColors.containsKey(owner)) {
            int seed = owner.hashCode();
            int hue = Math.abs(seed % 360);
            Paint color = Color.hsb(hue, 0.8, 0.9);
            userColors.put(owner, color);
        }
        return userColors.get(owner);
    }

    private void drawLegend(GraphicsContext gc, Map<String, Paint> userColors) {
        int legendX = (int) canvas.getWidth() - 100; // Позиция легенды
        int legendY = 50;

        for (String owner : userColors.keySet()) {
            Paint color = userColors.get(owner);

            // Рисуем цветовой блок
            gc.setFill(color);
            gc.fillRect(legendX, legendY, 20, 20);

            // Рисуем имя владельца
            gc.setFill(Color.BLACK);
            gc.fillText(owner, legendX + 30, legendY + 15);

            legendY += 30; // Сдвигаем следующий элемент
        }
    }

}