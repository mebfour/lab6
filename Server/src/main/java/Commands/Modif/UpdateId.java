package Commands.Modif;

import Classes.Coordinates;
import Classes.Location;
import Classes.Route;
import Classes.RouteDTO;
import Collection.RouteCollectionManager;
import Commands.Command;
import Commands.CommandResponse;
import com.google.gson.Gson;

import java.util.Date;
import java.util.Map;

import static Collection.RouteCollectionManager.routeList;
import static users.LoginCommand.username;

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
            RouteDTO dto = gson.fromJson(jsonArgs, RouteDTO.class);
            Route updatedRoute = new Route(
                    dto.getKey(),
                    dto.getId(),
                    dto.getName(),
                    dto.getOwner(),
                    new Coordinates(dto.getX(), dto.getY()),
                    new Date(dto.getCreationDate()), // преобразуем long -> Date
                    new Location(dto.getFromX(), dto.getFromY(), dto.getFromZ(), dto.getFromName()),
                    new Location(dto.getToX(), dto.getToY(), dto.getToZ(), dto.getToName())
            );
            int id = updatedRoute.getId();
            boolean findId = false;

            String key = updatedRoute.getKey();
            synchronized(routeList) {
                for (Map.Entry<String, Route> entry : routeList.entrySet()) {
                    if (entry.getValue().getKey().equals(key)) {
                        key = entry.getKey();
                        findId = true;
                        break;
                    }
                }
            }
            if (!findId) {
                return new CommandResponse("Элемент с таким id не найден.", false);
            }

            if (routeList.get(key).getOwner().equals(username)) {
                updatedRoute.setKey(key);

                collectionManager.updateToBD(updatedRoute);
                collectionManager.addToCollection(updatedRoute);
                return new CommandResponse("Элемент успешно обновлён.", true);
            }else {
                return new CommandResponse("Ошибка доступа: объект Вам не принадлежит", false);
            }

        } catch (Exception e) {
            //удали
            e.printStackTrace();
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
