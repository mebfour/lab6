package Commands;

import Classes.Route;
import InputHandler.InputProvider;
import InputHandler.inputObject;
import ToStart.CommandRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Scanner;
import java.util.function.Consumer;

import static ToStart.UserSession.currentUsername;

public class UpdateIdCommand implements ClientCommand {
    private final Gson gson;
    private final Consumer<String> sendMessage;
    private IdChecker checkIdOnServer;


    public UpdateIdCommand(Gson gson, Consumer<String> sendMessage, IdChecker checkIdOnServer) {
        this.gson = gson;
        this.sendMessage = sendMessage;
        this.checkIdOnServer = checkIdOnServer;
    }

    @Override
    public void clientExecute(String[] args, String pars, InputProvider provider, Scanner scanner) throws IOException {
        int id = -1;

        // Если id передан в аргументах команды
        if (args.length > 1) {
            try {
                id = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("id должен быть целым числом.");
            }
        }

        // Если id не передан или некорректен - запрашиваем у пользователя
        while (id < 0) {
            System.out.print("Введите id > 0: ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("id не может быть пустым. Повторите ввод.");
                continue;
            }
            try {
                id = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("id должен быть целым числом. Повторите ввод.");
            }
        }

        // Проверяем наличие id на сервере
        boolean exists = checkIdOnServer.checkIdOnServer(id);
        if (exists) {
            // Теперь запрашиваем новые данные для объекта
            Route updatedRoute = inputObject.inputObject(new Route(), provider);
            updatedRoute.setId(id);

            String updateJson = gson.toJson(updatedRoute);

            CommandRequest updateRequest = new CommandRequest("update_id", updateJson, currentUsername);
            sendMessage.accept(gson.toJson(updateRequest));
        }


    }

    @Override
    public String getName() {
        return "update_id";
    }
}
