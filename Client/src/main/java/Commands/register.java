package Commands;

import InputHandler.InputProvider;
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
import javafx.scene.control.*;

import static ToStart.UserSession.currentUsername;

public class Register implements ClientCommand {
    private final Gson gson;
    private final Consumer<String> sendMessage;
    private final TextField loginField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private Button loginButton = new Button("Зарегистрироваться");

    public Register(Gson gson, Consumer<String> sendMessage) {
        this.gson = gson;
        this.sendMessage = sendMessage;
        loginField.setPromptText("Логин");
        passwordField.setPromptText("Пароль");
        loginButton.setOnAction(e -> onRegister());
    }

    private void onRegister() {
        String username = loginField.getText();
        String password = passwordField.getText();
        CommandRequest request = new CommandRequest("login", username + " " + password, username);
        String json = new Gson().toJson(request);
        sendMessage.accept(json);
    }


    @Override
    public void clientExecute(String[] args, String pars) throws IOException{
        String password;
        // Получаем консоль
        Console console = System.console();
        char[] passwordChars = console.readPassword("Введите пароль: ");
        password = new String(passwordChars);

        try {
            password = PasswordUtil.hashPassword(password);
            Map<String, String> params = new HashMap<>();
            params.put("username", currentUsername);
            params.put("password", password);
            // Преобразуем параметры в JSON
            String jsonParams = gson.toJson(params);
            CommandRequest commandRequest = new CommandRequest("register", jsonParams, currentUsername);
            // Сериализуем запрос в JSON
            String jsonRequest = gson.toJson(commandRequest);
            // Отправляем запрос на сервер
            sendMessage.accept(jsonRequest);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Ошибка хэширования пароля");
        }
    }
    @Override
    public String getName() {
        return "register";
    }
}
