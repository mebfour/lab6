package Commands.BaseCom;

import Commands.Command;
import Commands.CommandResponse;

import static Collection.RouteCollectionManager.routeList;

public class CheckIdCommand implements Command {
    @Override
    public CommandResponse execute(String arg) {
        int id;
        try {
            id = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return new CommandResponse("Некорректный id", false);
        }

        boolean exists = routeList.values().stream()
                .anyMatch(route -> route.getId() == id);

        if (exists) {
            return new CommandResponse("Id найден", true);
        } else {
            return new CommandResponse("Id не найден", false);
        }
    }

    @Override
    public String getName() {
        return "check_id";
    }

    @Override
    public String getDescription() {
        return "проверяет, существует ли элемент с заданным id";
    }
}

