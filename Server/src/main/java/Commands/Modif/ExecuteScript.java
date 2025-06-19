package Commands.Modif;

import Classes.Coordinates;
import Classes.Location;
import Classes.Route;
import Collection.RouteCollectionManager;
import Commands.Command;
import Commands.CommandResponse;
import com.google.gson.Gson;

import java.util.Date;

public class ExecuteScript implements Command {
    private final RouteCollectionManager collectionManager;
    final Gson gson = new Gson();
    public ExecuteScript(RouteCollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public CommandResponse execute(String args) {
        System.out.println("аргументы: " + args);
        if (args == null || args.trim().isEmpty()) {
            return new CommandResponse("Скрипт пуст.", true);
        }

        // Разбиваем скрипт на строки и имитируем выполнение
        String[] lines = args.split("\\r?\\n");
        int lineNum = 0;

        try {
            for (String line : lines) {
                lineNum++;
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) continue;

                // Разбираем строку на части
                String[] parts = trimmedLine.split(" ", 2); // ["add", "{...}"]
                String commandName = parts[0];
                String arges = parts.length > 1 ? parts[1] : "";

                CommandResponse response = handleCommand(commandName, arges);
                if (!response.isSuccess()) {
                    throw new IllegalArgumentException(response.getMessage());
                }
            }

            return new CommandResponse("Скрипт успешно выполнен.", true);
        } catch (Exception e) {
            String errorMessage = "Ошибка на строке " + lineNum + ": " + e.getMessage();
            return new CommandResponse(errorMessage, false);
        }
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "исполняет скрипт";
    }

    public CommandResponse handleCommand(String commandName, String args) {
        try {
            Route route = new Route(
                    "script_route",
                    101,
                    "script_route",
                    "uyt",
                    new Coordinates(5L, 5),
                    new Date(),
                    new Location(5, 55, 555, "from"),
                    new Location(5, 55, 555, "to")
            );
            collectionManager.saveToBD(route);
            collectionManager.addToCollection(route);
            return new CommandResponse("Скрипт исполнен", true);
        } catch (Exception e) {
            //удали
            e.printStackTrace();
            return new CommandResponse("Ошибка добавления маршрута", false);
        }
    }
}
