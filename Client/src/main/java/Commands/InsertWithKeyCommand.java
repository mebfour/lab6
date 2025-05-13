package Commands;

import Classes.Route;
import InputHandler.InputProvider;
import InputHandler.inputObject;
import ToStart.CommandRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Scanner;
import java.util.function.Consumer;

public class InsertWithKeyCommand implements ClientCommand {
    private final Gson gson;
    private final Consumer<String> sendMessage;

    public InsertWithKeyCommand(Gson gson, Consumer<String> sendMessage) {
        this.gson = gson;
        this.sendMessage = sendMessage;

    }
    @Override
    public void clientExecute(String[] args, String pars, InputProvider provider, Scanner scanner) throws IOException {
        Route route = new Route();
        String key;
        if (args.length > 1) {
            key = args[1];
        } else {
            System.out.print("Введите ключ: ");
            key = scanner.nextLine().trim();
            while (key.isEmpty()) {
                System.out.println("Ключ не может быть пустым. Повторите ввод.");
                key = scanner.nextLine().trim();
            }
        }

        route.setKey(key);

        // Заполняем остальные поля объекта
        route = inputObject.inputObject(route, provider);

        // Сериализуем в JSON
        String jsonRoute = gson.toJson(route);

        // Формируем запрос
        CommandRequest commandRequest = new CommandRequest("add", jsonRoute);
        String jsonRequest = gson.toJson(commandRequest);

        // Отправляем на сервер
        sendMessage.accept(jsonRequest);
    }

    @Override
    public String getName() {
        return "insert_with_key";
    }
}
