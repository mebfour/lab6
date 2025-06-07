package Commands;

import InputHandler.InputProvider;
import ToStart.CommandRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Consumer;

import static ToStart.UserSession.currentUsername;

public class RemoveGreaterCommand implements ClientCommand {
    private final Gson gson;
    private final Consumer<String> sendMessage;

    public RemoveGreaterCommand(Gson gson, Consumer<String> sendMessage) {
        this.gson = gson;
        this.sendMessage = sendMessage;

    }
    @Override
    public void clientExecute(String[] args, String pars, InputProvider provider, Scanner scanner) throws IOException {
        String key;

        if (args.length >= 1 && args[0] != null && !args[0].trim().isEmpty()) {
            key = String.join(" ", Arrays.asList(args).subList(1, args.length));
        } else {
            while (true) {
                System.out.print("Введите ключ элемента: ");
                key = scanner.nextLine().trim();
                if (!key.isEmpty()) {
                    break;
                }
                System.out.println("Ключ не может быть пустым. Повторите ввод.");
            }
        }
        CommandRequest removeGreaterRequest = new CommandRequest("remove_greater", key, currentUsername);
        String jsonRequest = gson.toJson(removeGreaterRequest);
        sendMessage.accept(jsonRequest);
    }

    @Override
    public String getName() {
        return "remove_greater";
    }
}
