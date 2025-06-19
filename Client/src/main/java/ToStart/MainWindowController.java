package ToStart;

import Classes.RouteDTO;
import InputHandler.JsonToRouteMapper;
import InputHandler.RouteInputDialog;

import InputHandler.ScriptInputDialog;
import View.InfoTabContent;
import View.Localization;
import com.google.gson.Gson;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import View.MyBoundingBox;
import javafx.scene.control.TabPane;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;



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
    private final Map<String, Paint> userColors = new HashMap<>();
    private final Random random = new Random();

    private final Gson gson;
    private final ClientNetworkManager clientNetworkManager;
    Consumer<String> sendMessage;
    private final AtomicBoolean mapIsLoad = new AtomicBoolean(false);
    private boolean initialLoadDone = false;
    private final ObjectProperty<Map<String, RouteDTO>> routeMapProperty = new SimpleObjectProperty<>();

    public ObjectProperty<Map<String, RouteDTO>> routeMapProperty() {
        return routeMapProperty;
    }


    public MainWindowController(ClientNetworkManager clientNetworkManager, String username) {
        this.clientNetworkManager = clientNetworkManager;
        this.sendMessage = clientNetworkManager.getSendMessage();
        this.gson = clientNetworkManager.getGson();
        this.currentUser = username;
        this.canvas  = new Canvas(800, 300);
        this.canvas.setWidth(600);
        Localization.setLocale(new Locale("ru"));
        setupTable();
        Label userLabel = new Label("Пользователь: " + username);
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
            if (selectedRoute != null) {
                String key = selectedRoute.getKey();

                ChangeListener<CommandResponse> listener = new ChangeListener<>() {
                    @Override
                    public void changed(ObservableValue<? extends CommandResponse> obs, CommandResponse oldVal, CommandResponse newVal) {
                        if (newVal != null && newVal.isSuccess()) {
                            clientNetworkManager.sendGetRoutesCommand(); // Запрашиваем новые данные
                        }
                        clientNetworkManager.commandResponseProperty().removeListener(this); // Теперь this — это слушатель
                    }
                };

                clientNetworkManager.commandResponseProperty().addListener(listener);
                clientNetworkManager.sendCommand("remove_by_key", key, username);


            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ничего не выбрано");
                alert.setHeaderText(null);
                alert.setContentText("Выберите маршрут для удаления.");
                alert.showAndWait();
            }
            clientNetworkManager.loadRoutesFromMapAsync();
        });
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

                    }
                    // Отписываемся после первого срабатывания
                    clientNetworkManager.commandResponseProperty().removeListener(this);
                }
            };

            // Подписываем слушатель
            clientNetworkManager.commandResponseProperty().addListener(listener);
            clientNetworkManager.loadRoutesFromMapAsync();
        });

        HBox buttonBox = new HBox(10, addButton, removeButton, editButton, scriptButton);

        // Устанавливаем минимальную ширину таблицы и максимальную для растяжения
        tableView.setMinWidth(400);
        tableView.setMaxWidth(Double.MAX_VALUE);
        // Устанавливаем фиксированную ширину Canvas

        // --- Создаем HBox для горизонтального размещения --


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
        hBox.getChildren().addAll(tableView, canvas);

        BorderPane mainContent = new BorderPane();
        mainContent.setCenter(hBox);
        mainContent.setBottom(buttonBox);
        mainTab.setContent(mainContent);

// Создаем вторую вкладку


        InfoTabContent infoTabContent = new InfoTabContent(routeMapProperty());
        this.infoTab = infoTabContent.getTab(this.infoTab);

// Добавляем обе вкладки в TabPane
        tabPane.getTabs().addAll(mainTab, infoTab);
        ChoiceBox<Locale> languageSelector = new ChoiceBox<>();
        languageSelector.getItems().addAll(Localization.getSupportedLocales());

// Устанавливаем текущую локаль
        languageSelector.setValue(Locale.getDefault());

        languageSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Localization.setLocale(newVal);
                updateUILanguage(userLabel, addButton, editButton,removeButton, scriptButton); // Обновляем элементы интерфейса
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
                        routeMap = JsonToRouteMapper.parseJsonToRouteMap(jsonArgs);
                        Platform.runLater(this::updateTableAndCanvas);
                        System.out.println("Коллекция обновилась — обновляем UI");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

// --- Подписка на commandResponse (для remove_by_key и других команд) ---
        clientNetworkManager.commandResponseProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isSuccess() && (!initialLoadDone)) {
                // ВСЕГДА запрашиваем актуальные данные после успешной команды
                clientNetworkManager.sendGetRoutesCommand();
            }
        });
        clientNetworkManager.routeResponseProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String jsonArgs = newVal.getMessage();
                if (jsonArgs != null && jsonArgs.trim().startsWith("{")) {
                    try {
                        routeMap = JsonToRouteMapper.parseJsonToRouteMap(jsonArgs);
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
            //clientNetworkManager.sendGetRoutesCommand();
            clientNetworkManager.loadRoutesFromMapAsync();
        }

        // Рисуем простой фон на canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

    }
    private void updateUILanguage(Label userLabel, Button addButton, Button editButton, Button removeButton, Button scriptButton) {
        // Обновление текстовых меток
        userLabel.setText(Localization.getString("user_label") + currentUser);
        addButton.setText(Localization.getString("add"));
        removeButton.setText(Localization.getString("remove"));
        editButton.setText(Localization.getString("edit"));

        // Обновляем заголовки вкладок
        mainTab.setText(Localization.getString("routes"));
        infoTab.setText(Localization.getString("information"));

        // Обновление заголовков таблицы
        List<TableColumn<RouteDTO, ?>> columns = tableView.getColumns();
        for (TableColumn<RouteDTO, ?> col : columns) {
            String originalText = col.getText();
            switch (originalText) {
                case "ID": col.setText(Localization.getString("id")); break;
                case "Название": col.setText(Localization.getString("name")); break;
                case "X": col.setText(Localization.getString("x")); break;
                case "Y": col.setText(Localization.getString("y")); break;
                case "Владелец": col.setText(Localization.getString("owner")); break;
                case "Дата создания": col.setText(Localization.getString("creation_date")); break;
                case "From": col.setText(Localization.getString("from")); break;
                case "To": col.setText(Localization.getString("to")); break;
                case "Ключ": col.setText(Localization.getString("key")); break;
            }
        }

        // Обновление заголовков вложенных колонок
        for (TableColumn<RouteDTO, ?> parentCol : tableView.getColumns()) {
            if (parentCol instanceof TableColumn<?, ?>) {
                for (Object subColObj : parentCol.getColumns()) {
                    if (subColObj instanceof TableColumn<?, ?> subCol) {
                        String text = subCol.getText();
                        switch (text) {
                            case "From Name": subCol.setText(Localization.getString("from_name")); break;
                            case "To Name": subCol.setText(Localization.getString("to_name")); break;
                        }
                    }
                }
            }
        }

        drawRoutes(); // Перерисовываем легенду
    }
    public BorderPane getView() {
        return root;
    }
    private void updateTableAndCanvas() {
        ObservableList<RouteDTO> routeList = FXCollections.observableArrayList(routeMap.values());
        tableView.setItems(routeList);
        drawRoutes();
        this.routeMapProperty.set(routeMap); // <-- Уведомляем слушателей
    }
    private void setupTable() {
        // Колонка ID
        TableColumn<RouteDTO, Integer> idCol = new TableColumn<>(Localization.getString("id"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Колонка Название
        TableColumn<RouteDTO, String> nameCol = new TableColumn<>(Localization.getString("name"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Колонка X
        TableColumn<RouteDTO, Number> xCol = new TableColumn<>(Localization.getString("x"));
        xCol.setCellValueFactory(new PropertyValueFactory<>("x"));

        // Колонка Y
        TableColumn<RouteDTO, Number> yCol = new TableColumn<>(Localization.getString("y"));
        yCol.setCellValueFactory(new PropertyValueFactory<>("y"));

        // Колонка Владелец
        TableColumn<RouteDTO, String> ownerCol = new TableColumn<>(Localization.getString("owner"));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));

        // Колонка Дата создания
        TableColumn<RouteDTO, String> dateCol = new TableColumn<>(Localization.getString("creation_date"));
        dateCol.setCellValueFactory(data -> {
            RouteDTO route = data.getValue();
            return new SimpleStringProperty(
                    Localization.getDateFormat().format(new Date(route.getCreationDate()))
            );
        });

        // Колонка From
        TableColumn<RouteDTO, String> fromCol = new TableColumn<>(Localization.getString("from"));
        fromCol.getColumns().addAll(
                createSubColumn(Localization.getString("from_name"), "fromName"),
                createSubColumn("X", "fromX"),
                createSubColumn("Y", "fromY"),
                createSubColumn("Z", "fromZ")
        );

        // Колонка To
        TableColumn<RouteDTO, String> toCol = new TableColumn<>(Localization.getString("to"));
        toCol.getColumns().addAll(
                createSubColumn(Localization.getString("to_name"), "toName"),
                createSubColumn("X", "toX"),
                createSubColumn("Y", "toY"),
                createSubColumn("Z", "toZ")
        );

        // Колонка Ключ
        TableColumn<RouteDTO, String> keyCol = new TableColumn<>(Localization.getString("key"));
        keyCol.setCellValueFactory(new PropertyValueFactory<>("key"));

        // Добавляем все колонки
        tableView.getColumns().addAll(idCol, nameCol, xCol, yCol, ownerCol, dateCol, fromCol, toCol, keyCol);

        // Устанавливаем пустой список на старте
        tableView.setItems(FXCollections.observableArrayList());
    }
    private void updateTable() {
        ObservableList<RouteDTO> routeList = FXCollections.observableArrayList(routeMap.values());
        tableView.setItems(routeList);
    }
    private TableColumn<RouteDTO, String> createSubColumn(String title, String field) {
        TableColumn<RouteDTO, String> col = new TableColumn<>(title);
        col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getByKey(field)));
        return col;
    }

    private void drawRoutes() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Фон
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double scale = 10;
        userColors.clear(); // можно очищать или нет — зависит от поведения

        for (RouteDTO route : routeMap.values()) {
            double x = route.getX() * scale;
            double y = route.getY() * scale;
            String owner = route.getOwner();

            // Получаем или создаём цвет для владельца
            if (!userColors.containsKey(owner)) {
                userColors.put(owner, getRandomColor());
            }

            Paint color = userColors.get(owner);

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
    private void startPulseAnimation(GraphicsContext gc, double x, double y, Paint color) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(500),
                        e -> {
                            double scale = 1 + Math.sin(System.currentTimeMillis() / 300.0) * 0.5;
                            gc.setFill(color);
                            gc.fillOval(x - 5 * scale, y - 5 * scale, 10 * scale, 10 * scale);
                        }
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    private Paint getRandomColor() {
        return Color.hsb(random.nextInt(360), 0.8, 0.9);
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