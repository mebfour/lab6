package Commands;

import InputHandler.InputProvider;
import ToStart.MainWindowController;
import ToStart.PasswordUtil;
import com.google.gson.Gson;

import java.io.Console;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import ToStart.CommandRequest;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import static ToStart.UserSession.currentUsername;

public class Register implements ClientCommand {
    private final Gson gson;
    private final Consumer<String> sendMessage;
    private TextField loginField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Button loginButton = new Button("Зарегистрироваться");

    public Register(Gson gson, Consumer<String> sendMessage) {
        this.gson = gson;
        this.sendMessage = sendMessage;
        loginField.setPromptText("Логин");
        passwordField.setPromptText("Пароль");

        loginButton.setOnAction(e -> onRegister());

//        root.getChildren().addAll(new Label("Логин:"), loginField,
//                new Label("Пароль:"), passwordField,
//                loginButton);
//        root.setPadding(new Insets(20));
    }

    private void onRegister() {
        String username = loginField.getText();
        String password = passwordField.getText();

//        if (clientNetworkManager.authorize(new Scanner(System.in))){
//            openMainWindow(username);
//        }   else {
//            showAlert("Ошибка", "Не удалось войти");
//        }



        //  Здесь отправляем команду login через ClientNetworkManager
        CommandRequest request = new CommandRequest("login", username + " " + password, username);
        String json = new Gson().toJson(request);

        sendMessage.accept(json);
    }


    @Override
    public void clientExecute(String[] args, String pars, InputProvider provider, Scanner scanner) throws IOException{
        // Считываем логин
        System.out.print("Введите логин: ");
        String username = scanner.nextLine().trim();
        currentUsername = username;
        // Считываем пароль скрытым вводом
        String password;

        // Получаем консоль
        Console console = System.console();

        if (console != null) {
            // Скрытый ввод пароля
            char[] passwordChars = console.readPassword("Введите пароль: ");
            password = new String(passwordChars);
        } else {
            // Если консоль недоступна, вводим пароль обычным способом
            System.out.print("Введите пароль: ");
            password = scanner.nextLine();
        }
        try {
            password = PasswordUtil.hashPassword(password);
            Map<String, String> params = new HashMap<>();
            params.put("username", currentUsername);
            params.put("password", password);

            // Преобразуем параметры в JSON
            String jsonParams = gson.toJson(params);

            // Создаём запрос с командой "register" и параметрами
            CommandRequest commandRequest = new CommandRequest("register", jsonParams, currentUsername);

            // Сериализуем запрос в JSON
            String jsonRequest = gson.toJson(commandRequest);

            // Отправляем запрос на сервер
            sendMessage.accept(jsonRequest);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Ошибка хэширования пароля");
        }
        // Формируем объект с параметрами регистрации

    }
    private void openMainWindow(String username) {
        Stage stage = new Stage();
        //MainWindowController controller = new MainWindowController(clientNetworkManager, username);
        //Scene scene = new Scene(controller.getView(), 1000, 600);

        stage.setTitle("Главная страница " + username);
        //stage.setScene(scene);
        stage.show();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header, just the message
        alert.setContentText(message);
        alert.showAndWait();
    }
    @Override
    public String getName() {
        return "register";
    }
}
