package Commands;

import ToStart.CommandRequest;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

import static ToStart.UserSession.currentUsername;

public class ReplaceIfLoweCommand implements ClientCommand {
    private final Gson gson;
    private final Consumer<String> sendMessage;

    public ReplaceIfLoweCommand(Gson gson, Consumer<String> sendMessage) {
        this.gson = gson;
        this.sendMessage = sendMessage;

    }
    @Override
    public void clientExecute(String[] args, String par) throws IOException {
        String targetKey = "";
        String newKey = "";

        // Получаем ключ элемента, который хотим заменить
        if (args.length > 1 && args[0] != null && !args[0].trim().isEmpty()) {
            targetKey = String.join(" ", Arrays.asList(args).subList(1, args.length));
        } else {
            while (true) {

                if (!targetKey.isEmpty()) break;

            }

            // Получаем новый ключ
            if (args.length >= 2 && args[1] != null && !args[1].trim().isEmpty()) {
                newKey = args[1].trim();
            }
            String[] keys = {targetKey, newKey};
            // Собираем строку из двух ключей для передачи на сервер
            String replaceArg = String.join(" ", keys);
            CommandRequest replaceRequest = new CommandRequest("replace_if_lowe", replaceArg, currentUsername);
            sendMessage.accept(gson.toJson(replaceRequest));
        }
    }

    @Override
    public String getName() {
        return "replace_if_lowe";
    }
}
