package Commands.Modif;

import Classes.Route;
import Collection.RouteCollectionManager;
import Commands.Command;
import Commands.CommandResponse;
import com.google.gson.Gson;

import java.util.Map;

import static Collection.RouteCollectionManager.routeList;

public class UpdateId implements Command {
    private final RouteCollectionManager collectionManager;
    final Gson gson = new Gson();
    public UpdateId(RouteCollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }


    @Override
    public CommandResponse execute(String jsonArgs) {
        try {

            // Десериализация объекта с новыми данными и id
            Route updatedRoute = gson.fromJson(jsonArgs, Route.class);
            int id = updatedRoute.getId();
            boolean findId = false;

            String key = null;
            for (Map.Entry<String, Route> entry : routeList.entrySet()) {
                if (entry.getValue().getId() == id) {
                    key = entry.getKey();
                    findId = true;
                    break;
                }
            }
            if (!findId) {
                return new CommandResponse("Элемент с таким id не найден.", false);
            }


            updatedRoute.setKey(key);
            collectionManager.addToCollection(updatedRoute);


            collectionManager.saveToFile();
            return new CommandResponse("Элемент успешно обновлён.", true);

        } catch (Exception e) {
            return new CommandResponse("Ошибка обновления элемента.", false);
        }
    }


@Override
public String getName() {
    return "update_id";
}

@Override
public String getDescription() {
    return "обновляет значение элемента коллекции, id которого равен заданному";
}
}
