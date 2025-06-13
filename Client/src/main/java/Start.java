import ToStart.ClientNetworkManager;
import ToStart.MainWindowController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;


public class Start extends Application {
    private ClientNetworkManager clientNetworkManager = new ClientNetworkManager();



    @Override
    public void start(Stage primaryStage) {
        TextField loginField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button authButton = new Button("Войти/Зарегистрироваться");
        Label messageLabel = new Label();
        new Thread(() -> {
            try {
                clientNetworkManager.start("localhost", 5842);
            } catch (IOException e) {
                Platform.runLater(() -> {
                    messageLabel.setText("Ошибка запуска подключения: " + e.getMessage());
                });
            }
        }).start();



        authButton.setOnAction(event -> {
            String username = loginField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Введите логин и пароль");
                return;
            }

            // Асинхронно проверяем пользователя и выполняем login/register
            Task<Boolean> authTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    Boolean ans = clientNetworkManager.authenticate(username, password);
                    System.out.println(ans);
                    return ans;
                }
            };

            authTask.setOnSucceeded(e -> {
                Boolean authorized = authTask.getValue();
                if (authorized != null && authorized) {
                    messageLabel.setText("Успешный вход");

                    MainWindowController controller = new MainWindowController(clientNetworkManager, username);
                    Scene scene = new Scene(controller.getView(), 800, 600);
                    primaryStage.setTitle("Route Table Viewer");
                    primaryStage.setScene(scene);
                    primaryStage.show();
                    // Открыть главное окно
                } else {
                    messageLabel.setText("Ошибка авторизации/регистрации");
                }
            });


            authTask.setOnFailed(e -> {
                messageLabel.setText("Ошибка подключения к серверу");
            });

            new Thread(authTask).start();
        });

        VBox root = new VBox(10, new Label("Логин:"), loginField, new Label("Пароль:"), passwordField, authButton, messageLabel);
        root.setPadding(new Insets(20));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Авторизация");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}