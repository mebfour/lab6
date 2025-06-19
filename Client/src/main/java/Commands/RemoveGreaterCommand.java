package Commands;

import ToStart.CommandRequest;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
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
    public void clientExecute(String[] args, String pars) throws IOException {
        String key = "";
        if (args.length >= 1 && args[0] != null && !args[0].trim().isEmpty()) {
            key = String.join(" ", Arrays.asList(args).subList(1, args.length));
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
