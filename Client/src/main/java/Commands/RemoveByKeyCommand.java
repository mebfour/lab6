package Commands;

import ToStart.CommandRequest;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
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
    public void clientExecute(String[] args, String pars) throws IOException {
        if (args.length >= 1 && args[0] != null && !args[0].trim().isEmpty()) {
            key = String.join(" ", Arrays.asList(args).subList(1, args.length));
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
