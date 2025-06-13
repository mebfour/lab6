package ToStart;

import com.google.gson.Gson;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Insets;

import java.util.Scanner;
import java.util.function.Consumer;

public class AuthController {

    private final Consumer<String> sendMessage;

    private TextField loginField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Button loginButton = new Button("Войти");
    private VBox root = new VBox(10); // Корневой контейнер с отступами
    private ClientNetworkManager clientNetworkManager;


    public AuthController(Consumer<String> sendMessage) {
        this.sendMessage = sendMessage;
        loginField.setPromptText("Логин");
        passwordField.setPromptText("Пароль");

        loginButton.setOnAction(e -> onLogin());

        root.getChildren().addAll(new Label("Логин:"), loginField,
                new Label("Пароль:"), passwordField,
                loginButton);
        root.setPadding(new Insets(20));
    }


    private void onLogin() {
        String username = loginField.getText();
        String password = passwordField.getText();

        if (clientNetworkManager.authorize(new Scanner(System.in))){
            openMainWindow(username);
        }   else {
            showAlert("Ошибка", "Не удалось войти");
        }



        //  Здесь отправляем команду login через ClientNetworkManager
        CommandRequest request = new CommandRequest("login", username + " " + password, username);
        String json = new Gson().toJson(request);

        sendMessage.accept(json);
    }

    public Parent getView() {
        return root;
    }

     private void openMainWindow(String username) {
         Stage stage = new Stage();
         MainWindowController controller = new MainWindowController(clientNetworkManager, username);
         Scene scene = new Scene(controller.getView(), 1000, 600);
 
         stage.setTitle("Главная страница " + username);
         stage.setScene(scene);
         stage.show();
     }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header, just the message
        alert.setContentText(message);
        alert.showAndWait();
    }


}
