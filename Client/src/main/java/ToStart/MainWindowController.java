package ToStart;

import Classes.RouteDTO;
import InputHandler.JsonToRouteMapper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import paint.MyBoundingBox;

import java.util.*;

public class MainWindowController {

    private final BorderPane root = new BorderPane();
    private final ObservableList<RouteDTO> data = FXCollections.observableArrayList();
    private final TableView<RouteDTO> tableView = new TableView<>(data);
    private final ClientNetworkManager clientNetworkManager;
    private final String currentUser;
    private Map<String, RouteDTO> routeMap = new LinkedHashMap<>();
    private Canvas canvas;
    private final Map<String, Paint> userColors = new HashMap<>();
    private final Random random = new Random();

    public MainWindowController(ClientNetworkManager clientNetworkManager, String username) {
        this.clientNetworkManager = clientNetworkManager;
        this.currentUser = username;
        this.canvas  = new Canvas(800, 600);
        this.canvas.setWidth(600);
        setupTable();
        Label userLabel = new Label("Пользователь: " + username);
        Button addButton = new Button("Добавить");
        Button removeButton = new Button("Удалить");

        HBox buttonBox = new HBox(10, addButton, removeButton);

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
        drawRoutes();

        //  HBox для горизонтального размещения ---
        HBox hBox = new HBox(10); // 10 — отступ между элементами
        hBox.getChildren().addAll(tableView, canvas);

// Размещаем элементы в BorderPane
        root.setTop(userLabel);
        root.setCenter(hBox);
        root.setBottom(buttonBox);


        // Подписка на изменение routeResponse
        clientNetworkManager.routeResponseProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String jsonArgs = newVal.getMessage();
                if (jsonArgs != null && jsonArgs.trim().startsWith("{")) {
                    try {
                        routeMap = JsonToRouteMapper.parseJsonToRouteMap(jsonArgs);
                        Platform.runLater(() -> {
                            updateTable();
                            drawRoutes(); // Обновляем Canvas
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Ошибка парсинга JSON");
                    }
                }
            }
        });

// Перед загрузкой маршрутов подписываемся на ответ команды
        clientNetworkManager.commandResponseProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isSuccess()) {
                String json = newVal.getMessage();
                System.out.println("JSON из commandResponse: " + json);
                if (json != null && !json.trim().isEmpty()) {
                    try {
                        System.out.println("Попытка парсить JSON: " + json);
                        routeMap = JsonToRouteMapper.parseJsonToRouteMap(json);
                        System.out.println("Размер routeMap после парсинга: " + routeMap.size());
                        Platform.runLater(() -> updateTable());
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Ошибка парсинга JSON");
                    }
                }
            }
        });


        clientNetworkManager.loadRoutesFromMapAsync();


        // Рисуем простой фон на canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());



    }

    public BorderPane getView() {
        return root;
    }

    private void setupTable() {
        // Настройка колонок происходит один раз
        TableColumn<RouteDTO, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<RouteDTO, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<RouteDTO, Number> xCol = new TableColumn<>("X");
        xCol.setCellValueFactory(new PropertyValueFactory<>("x"));

        TableColumn<RouteDTO, Number> yCol = new TableColumn<>("Y");
        yCol.setCellValueFactory(new PropertyValueFactory<>("y"));

        TableColumn<RouteDTO, String> ownerCol = new TableColumn<>("Владелец");
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("owner"));

        TableColumn<RouteDTO, String> dateCol = new TableColumn<>("Дата создания");
        dateCol.setCellValueFactory(data -> {
            RouteDTO route = data.getValue();
            return new SimpleStringProperty(
                    new Date(route.getCreationDate()).toString()
            );
        });

// Колонка "From" — объединяем fromX, fromY, fromZ, fromName
        TableColumn<RouteDTO, String> fromCol = new TableColumn<>("From");
        fromCol.getColumns().addAll(
                createSubColumn("From X", "fromX"),
                createSubColumn("From Y", "fromY"),
                createSubColumn("From Z", "fromZ"),
                createSubColumn("From Name", "fromName")
        );

// Колонка "To" — объединяем toX, toY, toZ, toName
        TableColumn<RouteDTO, String> toCol = new TableColumn<>("To");
        toCol.getColumns().addAll(
                createSubColumn("To X", "toX"),
                createSubColumn("To Y", "toY"),
                createSubColumn("To Z", "toZ"),
                createSubColumn("To Name", "toName")
        );

        TableColumn<RouteDTO, String> keyCol = new TableColumn<>("Ключ");
        keyCol.setCellValueFactory(new PropertyValueFactory<>("key"));
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

        // Очищаем Canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Рисуем фон
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Масштабирование координат
        double scale = 10; // Увеличиваем координаты в 10 раз

        // Цвета по владельцам
        Map<String, Paint> userColors = new HashMap<>();

        for (RouteDTO route : routeMap.values()) {
            double x = route.getX() * scale;
            double y = route.getY() * scale;
            String owner = route.getOwner();

            // Получаем цвет для владельца
            userColors.putIfAbsent(owner, getRandomColor());
            Paint color = userColors.get(owner);

            // Рисуем точку
            gc.setFill(color);
            gc.fillOval(x - 5, y - 5, 10, 10);

            // Добавляем текст: ID маршрута
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(route.getId()), x + 8, y + 4);

            // Сохраняем "объект" как прямоугольник для проверки клика
            route.setBoundingBox(new MyBoundingBox(x, y, 10, 10, route));
        }

        // Рисуем легенду справа
        drawLegend(gc, userColors);
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