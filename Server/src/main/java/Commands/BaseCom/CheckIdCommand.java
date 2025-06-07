package Commands.BaseCom;

import Classes.Route;
import Commands.Command;
import Commands.CommandResponse;

import java.util.Optional;

import static Collection.RouteCollectionManager.routeList;
import static users.LoginCommand.username;

public class CheckIdCommand implements Command {
    @Override
    public CommandResponse execute(String arg) {
        int id;
        try {
            id = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return new CommandResponse("Некорректный id", false);
        }
        Optional<Route> routeOpt = routeList.values().stream()
                .filter(route -> route.getId() == id)
                .findFirst();

        boolean exists = routeList.values().stream()
                .anyMatch(route -> route.getId() == id);

        if (exists) {
            Route route = routeOpt.get();

            if (route.getOwner().equals(username)) {
                return new CommandResponse("Id найден", true);
            }else {
                return new CommandResponse("Ошибка доступа: объект Вам не принадлежит", false);
            }



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

