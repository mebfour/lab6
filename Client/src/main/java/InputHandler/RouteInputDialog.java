package InputHandler;
import Classes.Coordinates;
import Classes.Location;
import Classes.Route;
import Classes.RouteDTO;
import ToStart.CommandRequest;
import View.Localization;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import com.google.gson.Gson;
import java.util.Optional;
import java.util.function.Consumer;

public class RouteInputDialog {
    private final Dialog<Route> dialog;
    private final String currentUsername;
    private final Gson gson;
    private final Consumer<String> sendMessage;

    public RouteInputDialog(String currentUsername, Gson gson, Consumer<String> sendMessage) {
        this.currentUsername = currentUsername;
        this.gson = gson;
        this.sendMessage = sendMessage;
        this.dialog = createDialog();
    }

    // Поля ввода
    private TextField nameField;
    private TextField coordXField;
    private TextField coordYField;
    private TextField toNameField;
    private TextField toXField;
    private TextField toYField;
    private TextField toZField;
    private TextField fromNameField;
    private TextField fromXField;
    private TextField fromYField;
    private TextField fromZField;

    private Dialog<Route> createDialog() {
        Dialog<Route> dialog = new Dialog<>();
        dialog.setTitle(Localization.getString("add_new_route"));
        dialog.setHeaderText(Localization.getString("fill in all fields"));

        ButtonType addButtonType = new ButtonType(Localization.getString("add"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(createInputGrid());

        // сразу диалог не закрываем
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Route route = createRouteFromInput();
                if (route != null) {
                    return route; // всё ок — возвращаем маршрут
                } else {
                    showInputErrorAlert(); // ошибка — не закрываем диалог
                    return null;
                }
            }
            return null; // для CANCEL и других кнопок
        });
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);

        addButton.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> nameField.getText().trim().isEmpty() ||
                                coordXField.getText().trim().isEmpty() ||
                                coordYField.getText().trim().isEmpty() ||
                                toNameField.getText().trim().isEmpty() ||
                                toXField.getText().trim().isEmpty() ||
                                toYField.getText().trim().isEmpty() ||
                                toZField.getText().trim().isEmpty() ||
                                fromNameField.getText().trim().isEmpty() ||
                                fromXField.getText().trim().isEmpty() ||
                                fromYField.getText().trim().isEmpty() ||
                                fromZField.getText().trim().isEmpty(),
                        nameField.textProperty(),
                        coordXField.textProperty(),
                        coordYField.textProperty(),
                        toNameField.textProperty(),
                        toXField.textProperty(),
                        toYField.textProperty(),
                        toZField.textProperty(),
                        fromNameField.textProperty(),
                        fromXField.textProperty(),
                        fromYField.textProperty(),
                        fromZField.textProperty()
                )
        );
        return dialog;
    }

    public void setRouteData(RouteDTO route) {
        nameField.setText(route.getName());
        coordXField.setText(String.valueOf(route.getX()));
        coordYField.setText(String.valueOf(route.getY()));
        fromNameField.setText(route.getFromName());
        fromXField.setText(String.valueOf(route.getFromX()));
        fromYField.setText(String.valueOf(route.getFromY()));
        fromZField.setText(String.valueOf(route.getFromZ()));
        toNameField.setText(route.getToName());
        toXField.setText(String.valueOf(route.getToX()));
        toYField.setText(String.valueOf(route.getToY()));
        toZField.setText(String.valueOf(route.getToZ()));
    }

    private GridPane createInputGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // создание и добавленме всех полей ввода
        nameField = new TextField();
        coordXField = new TextField();
        coordYField = new TextField();
        toNameField = new TextField();
        toXField = new TextField();
        toYField = new TextField();
        toZField = new TextField();
        fromNameField = new TextField();
        fromXField = new TextField();
        fromYField = new TextField();
        fromZField = new TextField();

        // подсказки для полей
        nameField.setPromptText(Localization.getString("name"));
        coordXField.setPromptText(Localization.getString("x_coord_l"));
        coordYField.setPromptText(Localization.getString("y_coord_i"));
        toNameField.setPromptText(Localization.getString("name_to"));
        toXField.setPromptText(Localization.getString("x_coord_f"));
        toYField.setPromptText(Localization.getString("y_coord_i"));
        toZField.setPromptText(Localization.getString("z_coord_i"));
        fromNameField.setPromptText(Localization.getString("name_from"));
        fromXField.setPromptText(Localization.getString("x_coord_f"));
        fromYField.setPromptText(Localization.getString("y_coord_i"));
        fromZField.setPromptText(Localization.getString("z_coord_i"));

        // Добавляем компоненты на панель
        int row = 0;

        // Основная информация
        grid.add(new Label(Localization.getString("main_info:")), 0, row++, 2, 1);
        grid.add(new Label(Localization.getString("route_name:")), 0, row);
        grid.add(nameField, 1, row++);

        // Координаты
        grid.add(new Label(Localization.getString("coords:")), 0, row++, 2, 1);
        grid.add(new Label("X:"), 0, row);
        grid.add(coordXField, 1, row++);
        grid.add(new Label("Y:"), 0, row);
        grid.add(coordYField, 1, row++);

        // Локация "Куда"
        grid.add(new Label(Localization.getString("to_place:")), 0, row++, 2, 1);
        grid.add(new Label(Localization.getString("name")+':'), 0, row);
        grid.add(toNameField, 1, row++);
        grid.add(new Label("X:"), 0, row);
        grid.add(toXField, 1, row++);
        grid.add(new Label("Y:"), 0, row);
        grid.add(toYField, 1, row++);
        grid.add(new Label("Z:"), 0, row);
        grid.add(toZField, 1, row++);

        // Локация "Откуда"
        grid.add(new Label("Пункт отправления:"), 0, row++, 2, 1);
        grid.add(new Label(Localization.getString("name")+':'), 0, row);
        grid.add(fromNameField, 1, row++);
        grid.add(new Label("X:"), 0, row);
        grid.add(fromXField, 1, row++);
        grid.add(new Label("Y:"), 0, row);
        grid.add(fromYField, 1, row++);
        grid.add(new Label("Z:"), 0, row);
        grid.add(fromZField, 1, row);

        // Добавляем валидацию для числовых полей
        addNumericValidation(coordXField, "\\d*");
        addNumericValidation(coordYField, "\\d*");
        addNumericValidation(toXField, "[\\d.]*");
        addNumericValidation(toYField, "\\d*");
        addNumericValidation(toZField, "\\d*");
        addNumericValidation(fromXField, "[\\d.]*");
        addNumericValidation(fromYField, "\\d*");
        addNumericValidation(fromZField, "\\d*");

        return grid;
    }

    // для добавления валидации числовых полей
    private void addNumericValidation(TextField field, String regex) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(regex)) {
                field.setText(oldValue);
            }
        });
    }

    private Route createRouteFromInput() {
        try {
            String name = nameField.getText();
            if (name == null || name.trim().isEmpty()) return null;

            Long coordX = Long.valueOf(coordXField.getText());
            Integer coordY = Integer.valueOf(coordYField.getText());

            String toName = toNameField.getText();
            Float toX = Float.valueOf(toXField.getText());
            Integer toY = Integer.valueOf(toYField.getText());
            Integer toZ = Integer.valueOf(toZField.getText());

            String fromName = fromNameField.getText();
            Float fromX = Float.valueOf(fromXField.getText());
            Integer fromY = Integer.valueOf(fromYField.getText());
            Integer fromZ = Integer.valueOf(fromZField.getText());

            Route route = new Route();
            route.setName(name);
            route.setKey(name);

            Coordinates coordinates = new Coordinates();
            coordinates.setX(coordX);
            coordinates.setY(coordY);
            route.setCoordinates(coordinates);

            Location toLocation = new Location();
            toLocation.setName(toName);
            toLocation.setX(toX);
            toLocation.setY(toY);
            toLocation.setZ(toZ);
            route.setTo(toLocation);

            Location fromLocation = new Location();
            fromLocation.setName(fromName);
            fromLocation.setX(fromX);
            fromLocation.setY(fromY);
            fromLocation.setZ(fromZ);
            route.setFrom(fromLocation);

            route.setOwner(currentUsername);

            return route;

        } catch (NumberFormatException | NullPointerException e) {
            return null; // Ошибка форматирования
        }
    }

    private void showInputErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Localization.getString("inp_err"));
        alert.setHeaderText(null);
        alert.setContentText(Localization.getString("check_correct"));
        alert.showAndWait();
    }

    public void showAndSendAdd() {
        Optional<Route> result = dialog.showAndWait();

        if (result.isPresent()) {
            Route route = result.get();
            route.setOwner(currentUsername);
            String jsonRoute = gson.toJson(convertToDTO(route));
            CommandRequest commandRequest = new CommandRequest("add", jsonRoute, currentUsername);
            RouteDTO dto = convertToDTO(route);
            dto.setOwner(currentUsername);
            String jsonRequest = gson.toJson(commandRequest);
            sendMessage.accept(jsonRequest);
        }
    }

    public static RouteDTO convertToDTO(Route route) {
        RouteDTO dto = new RouteDTO(
                route.getId(),
                route.getName(),
                route.getCoordinates().getX(),
                route.getCoordinates().getY(),
                route.getOwner(),
                route.getCreationDate().getTime(),
                route.getFrom().getX(),
                route.getFrom().getY(),
                route.getFrom().getZ(),
                route.getFrom().getName(),
                route.getTo().getX(),
                route.getTo().getY(),
                route.getTo().getZ(),
                route.getTo().getName(),
                route.getKey()
        );
        dto.setOwner(route.getOwner());
        return dto;
    }
    public void showAndSendUpdate(RouteDTO oldRoute) {
        Optional<Route> result = dialog.showAndWait();

        if (result.isPresent()) {
            Route route = result.get();
            route.setOwner(currentUsername);
            route.setId(oldRoute.getId());
            route.setKey(oldRoute.getKey());
            String updateJson = gson.toJson(convertToDTO(route));
            System.out.println(updateJson);
            CommandRequest updateRequest = new CommandRequest("update_id", updateJson, currentUsername);
            sendMessage.accept(gson.toJson(updateRequest));
        }
    }
}