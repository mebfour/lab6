package ToStart;

import Classes.Route;
import Classes.RouteDTO;
import InputHandler.JsonToRouteMapper;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainWindowController {

    private final BorderPane root = new BorderPane();
    private final ObservableList<RouteDTO> data = FXCollections.observableArrayList();
    private final TableView<RouteDTO> tableView = new TableView<>(data);
    private final Canvas canvas = new Canvas(300, 500);
    private final ClientNetworkManager clientNetworkManager;
    private final String currentUser;
    private Map<String, RouteDTO> routeMap = new LinkedHashMap<>();

    public MainWindowController(ClientNetworkManager clientNetworkManager, String username) {
        this.clientNetworkManager = clientNetworkManager;
        this.currentUser = username;

        // Подписка на изменение routeResponse
        clientNetworkManager.routeResponseProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String jsonArgs = newVal.getMessage();
                System.out.println("JSON из routeResponse: " + jsonArgs);
                if (jsonArgs != null && jsonArgs.trim().startsWith("{")) {
                    try {
                        routeMap = JsonToRouteMapper.parseJsonToRouteMap(jsonArgs);
                        Platform.runLater(() -> updateTable());
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




        Label userLabel = new Label("Пользователь: " + username);

        setupTable();

        Button addButton = new Button("Добавить");
        Button removeButton = new Button("Удалить");

        HBox buttonBox = new HBox(10, addButton, removeButton);

        // Рисуем простой фон на canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Размещаем элементы
        root.setTop(userLabel);
        root.setCenter(tableView);
        root.setRight(canvas);
        root.setBottom(buttonBox);
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

}