package Commands;

import Classes.Route;
import InputHandler.InputProvider;
import ToStart.CommandRequest;
import com.google.gson.Gson;
import InputHandler.inputObject;
import java.io.IOException;
import java.util.Scanner;
import java.util.function.Consumer;

public class AddCommand implements ClientCommand {
    private final Gson gson;
    private final Consumer<String> sendMessage;

    public AddCommand(Gson gson, Consumer<String> sendMessage) {
        this.gson = gson;
        this.sendMessage = sendMessage;

    }

    @Override
    public void clientExecute(String[] args, String pars, InputProvider provider, Scanner scanner) throws IOException {
        Route route = new Route();
        route = inputObject.inputObject(route, provider);

        String jsonRoute = gson.toJson(route);
        CommandRequest commandRequest = new CommandRequest("add", jsonRoute);
        String jsonRequest = gson.toJson(commandRequest);

        sendMessage.accept(jsonRequest);
    }

    @Override
    public String getName() {
        return "add";
    }


}