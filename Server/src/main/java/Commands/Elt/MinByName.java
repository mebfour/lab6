package Commands.Elt;
import Classes.Route;
import Commands.Command;
import Commands.CommandResponse;

import com.google.gson.Gson;

import java.util.Comparator;

import static Collection.RouteCollectionManager.routeList;

public class MinByName implements Command {
    @Override
    public CommandResponse execute(String args) {
        if (routeList.isEmpty()) {
            return new CommandResponse("Коллекция пуста! Введите add для добавления нового элемента.", false);
        }
        Route minRoute = routeList.values()
                .stream()
                .min(Comparator.comparing(Route::getName))
                .orElse(null);

        if (minRoute == null) {
            return new CommandResponse("Не удалось найти элемент с минимальным name.", false);
        }

        String json = new Gson().toJson(minRoute);
        return new CommandResponse(json, true);
    }

    @Override
    public String getName() {
        return "min_by_name";
    }

    @Override
    public String getDescription() {
        return "выводит любой объект из коллекции, значение поля name которого является минимальным";
    }
}

