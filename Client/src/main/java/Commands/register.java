package Commands;

import InputHandler.InputProvider;
import com.google.gson.Gson;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import ToStart.CommandRequest;

public class register implements ClientCommand {
    private final Gson gson;
    private final Consumer<String> sendMessage;

    public register(Gson gson, Consumer<String> sendMessage) {
        this.gson = gson;
        this.sendMessage = sendMessage;
    }


    @Override
    public void clientExecute(String[] args, String pars, InputProvider provider, Scanner scanner) throws IOException {
        // Считываем логин
        System.out.print("Введите логин: ");
        String username = scanner.nextLine().trim();

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

        // Формируем объект с параметрами регистрации
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        // Преобразуем параметры в JSON
        String jsonParams = gson.toJson(params);

        // Создаём запрос с командой "register" и параметрами
        CommandRequest commandRequest = new CommandRequest("register", jsonParams);

        // Сериализуем запрос в JSON
        String jsonRequest = gson.toJson(commandRequest);

        // Отправляем запрос на сервер
        sendMessage.accept(jsonRequest);
    }

    @Override
    public String getName() {
        return "register";
    }
}
