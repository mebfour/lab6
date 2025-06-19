package InputHandler;
import ToStart.CommandRequest;
import View.Localization;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import com.google.gson.Gson;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class ScriptInputDialog {
    private final Dialog<String> dialog;
    private final String currentUsername;
    private final Gson gson;
    private final Consumer<String> sendMessage;

    private TextField filePathField;

    public ScriptInputDialog(String currentUsername, Gson gson, Consumer<String> sendMessage) {
        this.currentUsername = currentUsername;
        this.gson = gson;
        this.sendMessage = sendMessage;
        this.dialog = createDialog();
    }

    private Dialog<String> createDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(Localization.getString("execute_script"));
        dialog.setHeaderText(Localization.getString("fill in all fields"));

        ButtonType scriptButtonType = new ButtonType(Localization.getString("script"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(scriptButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(createInputGrid());

        // Кастомная обработка нажатия OK
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == scriptButtonType) {
                sendScriptFromFilePath();
                return null; // Закрываем диалог без результата , тк отправка уже выполнена
            }
            return null;
        });

        Node scriptButton = dialog.getDialogPane().lookupButton(scriptButtonType);
        scriptButton.disableProperty().bind(
                filePathField.textProperty().isEmpty()
        );

        return dialog;
    }

    private GridPane createInputGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        filePathField = new TextField();
        filePathField.setPromptText(Localization.getString("tip_path"));

        grid.add(new Label(Localization.getString("tip_path")), 0, 0);
        grid.add(filePathField, 1, 0);

        return grid;
    }

    private void sendScriptFromFilePath() {
        String path = filePathField.getText().trim();

        if (path.isEmpty()) {
            showErrorAlert("Ошибка", "Укажите путь к файлу.");
            return;
        }

        try {
            String scriptContent = new String(Files.readAllBytes(Paths.get(path)));

            // Создаем команду execute_script и отправляем
            CommandRequest request = new CommandRequest("execute_script", scriptContent, currentUsername);
            String jsonRequest = gson.toJson(request);
            sendMessage.accept(jsonRequest);
            showSuccessAlert();

        } catch (FileNotFoundException e) {
            showErrorAlert("Ошибка файла", "Файл не найден: " + path);
        } catch (IOException e) {
            showErrorAlert("Ошибка чтения", "Не удалось прочитать файл: " + path);
        } catch (Exception e) {
            showErrorAlert("Непредвиденная ошибка", e.getMessage());
        }
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");
        alert.setHeaderText(null);
        alert.setContentText("Скрипт успешно отправлен на сервер.");
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showAndSend() {
        dialog.showAndWait();
    }
}