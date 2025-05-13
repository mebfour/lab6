package Commands;

import InputHandler.InputProvider;
import ToStart.CommandRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Consumer;

public class RemoveLowerCommand implements ClientCommand {
    private final Gson gson;
    private final Consumer<String> sendMessage;

    public RemoveLowerCommand(Gson gson, Consumer<String> sendMessage) {
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
        CommandRequest lowerRequest = new CommandRequest("remove_lower", key);
        sendMessage.accept(gson.toJson(lowerRequest));
    }

    @Override
    public String getName() {
        return "remove_lower";
    }
}
