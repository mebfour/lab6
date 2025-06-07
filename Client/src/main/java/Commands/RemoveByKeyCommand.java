package Commands;

import InputHandler.InputProvider;
import ToStart.CommandRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Consumer;

import static ToStart.UserSession.currentUsername;

public class RemoveByKeyCommand implements ClientCommand {
    private final Gson gson;
    private final Consumer<String> sendMessage;
    String key;

    public RemoveByKeyCommand(Gson gson, Consumer<String> sendMessage) {
        this.gson = gson;
        this.sendMessage = sendMessage;
    }

    @Override
    public void clientExecute(String[] args, String pars, InputProvider provider, Scanner scanner) throws IOException {
        if (args.length >= 1 && args[0] != null && !args[0].trim().isEmpty()) {
            key = String.join(" ", Arrays.asList(args).subList(1, args.length));
        } else {
            // Запрашиваем ввод с консоли
            while (true) {
                System.out.print("Введите ключ элемента, который Вы хотите удалить: ");
                key = scanner.nextLine().trim();
                if (!key.isEmpty()) {
                    break;
                }
                System.out.println("Ключ не может быть пустым. Повторите ввод.");
            }
        }
        CommandRequest removeRequest = new CommandRequest("remove_by_key", key, currentUsername);
        String removeJsonRequest = gson.toJson(removeRequest);

        sendMessage.accept(removeJsonRequest);
    }

    @Override
    public String getName() {
        return "remove_by_key";
    }
}
