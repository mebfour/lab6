package InputHandler;
import Classes.Coordinates;
import Classes.Location;
import Classes.Route;
import Classes.RouteDTO;
import ToStart.CommandRequest;
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
        dialog.setTitle("Добавление нового маршрута");
        dialog.setHeaderText("Заполните все поля");

        ButtonType addButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(createInputGrid());

        // НЕ ЗАКРЫВАЙТЕ ДИАЛОГ СРАЗУ — делаем кастомный конвертер
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

    private GridPane createInputGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Создаем и добавляем все поля ввода
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

        // Добавляем подсказки для полей
        nameField.setPromptText("Название маршрута");
        coordXField.setPromptText("Координата X (long)");
        coordYField.setPromptText("Координата Y (int)");
        toNameField.setPromptText("Название пункта назначения");
        toXField.setPromptText("Координата X (float)");
        toYField.setPromptText("Координата Y (int)");
        toZField.setPromptText("Координата Z (int)");
        fromNameField.setPromptText("Название пункта отправления");
        fromXField.setPromptText("Координата X (float)");
        fromYField.setPromptText("Координата Y (int)");
        fromZField.setPromptText("Координата Z (int)");

        // Добавляем компоненты на панель
        int row = 0;

        // Основная информация
        grid.add(new Label("Основная информация:"), 0, row++, 2, 1);
        grid.add(new Label("Название маршрута:"), 0, row);
        grid.add(nameField, 1, row++);

        // Координаты
        grid.add(new Label("Координаты:"), 0, row++, 2, 1);
        grid.add(new Label("X:"), 0, row);
        grid.add(coordXField, 1, row++);
        grid.add(new Label("Y:"), 0, row);
        grid.add(coordYField, 1, row++);

        // Локация "Куда"
        grid.add(new Label("Пункт назначения:"), 0, row++, 2, 1);
        grid.add(new Label("Название:"), 0, row);
        grid.add(toNameField, 1, row++);
        grid.add(new Label("Координата X:"), 0, row);
        grid.add(toXField, 1, row++);
        grid.add(new Label("Координата Y:"), 0, row);
        grid.add(toYField, 1, row++);
        grid.add(new Label("Координата Z:"), 0, row);
        grid.add(toZField, 1, row++);

        // Локация "Откуда"
        grid.add(new Label("Пункт отправления:"), 0, row++, 2, 1);
        grid.add(new Label("Название:"), 0, row);
        grid.add(fromNameField, 1, row++);
        grid.add(new Label("Координата X:"), 0, row);
        grid.add(fromXField, 1, row++);
        grid.add(new Label("Координата Y:"), 0, row);
        grid.add(fromYField, 1, row++);
        grid.add(new Label("Координата Z:"), 0, row);
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

    // Метод для добавления валидации числовых полей
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
        alert.setTitle("Ошибка ввода");
        alert.setHeaderText(null);
        alert.setContentText("Проверьте правильность введенных числовых значений");
        alert.showAndWait();
    }

    public void showAndSend() {
        Optional<Route> result = dialog.showAndWait();

        if (result.isPresent()) {
            Route route = result.get();
            System.out.println("user " + currentUsername);
            route.setOwner(currentUsername);
            String jsonRoute = gson.toJson(convertToDTO(route));
            CommandRequest commandRequest = new CommandRequest("add", jsonRoute, currentUsername);
            RouteDTO dto = convertToDTO(route);
            dto.setOwner(currentUsername);

            System.out.println("Route owner: " + route.getOwner());
            System.out.println("DTO owner: " + dto.getOwner()); // должен вывести имя пользователя
            String json = gson.toJson(dto);
            System.out.println("JSON: " + json); // должен содержать "owner":"..."
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
}