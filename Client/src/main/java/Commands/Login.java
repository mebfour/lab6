package Commands;

import InputHandler.InputProvider;
import ToStart.CommandRequest;
import com.google.gson.Gson;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class Login implements ClientCommand{
    private final Gson gson;
    private final Consumer<String> sendMessage;

    public Login(Gson gson, Consumer<String> sendMessage) {
        this.gson = gson;
        this.sendMessage = sendMessage;
    }

    @Override
    public void clientExecute(String[] args, String pars, InputProvider provider, Scanner scanner) throws IOException {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine().trim();

        // Считываем пароль скрытым вводом
        String password;

        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword("Введите пароль: ");
            password = new String(passwordChars);
        } else {
            System.out.print("Введите пароль: ");
            password = scanner.nextLine();
        }

        // Формируем параметры в Map
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        // Сериализуем параметры в JSON
        String jsonParams = gson.toJson(params);

        // Создаём объект запроса с командой "login"
        CommandRequest commandRequest = new CommandRequest("login", jsonParams);

        // Сериализуем запрос в JSON
        String jsonRequest = gson.toJson(commandRequest);

        // Отправляем запрос на сервер
        sendMessage.accept(jsonRequest);
    }


    @Override
    public String getName() {
        return "login";
    }
}
