package Commands.Elt;

import Classes.Route;
import Commands.Command;
import Commands.CommandResponse;


import com.google.gson.Gson;

import java.util.Comparator;

import static Collection.RouteCollectionManager.routeList;

public class MaxByID implements Command {
    @Override
    public CommandResponse execute(String args) {
        if (routeList.isEmpty()) {
            return new CommandResponse("Коллекция пуста! Введите add для добавления нового элемента.", false);
        }
        Route maxRoute = routeList.values()
                .stream()
                .max(Comparator.comparingInt(Route::getId))
                .orElse(null);

        if (maxRoute == null) {
            return new CommandResponse("Не удалось найти элемент с максимальным id.", false);
        }

        // Сериализуем объект в JSON для отправки клиенту
        String json = new Gson().toJson(maxRoute);
        return new CommandResponse(json, true);
    }

    @Override
    public String getName() {
        return "max_by_id";
    }

    @Override
    public String getDescription() {
        return "выводит любой объект из коллекции, значение поля id которого является максимальным";
    }
}
