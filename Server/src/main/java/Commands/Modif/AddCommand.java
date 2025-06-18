package Commands.Modif;

import Classes.Coordinates;
import Classes.Location;
import Classes.Route;
import Classes.RouteDTO;
import Collection.RouteCollectionManager;
import Commands.Command;
import Commands.CommandResponse;
import com.google.gson.Gson;
import sql.DataSourceProvider;
import javax.sql.DataSource;
import java.util.Date;

public class AddCommand implements Command {
    private final RouteCollectionManager collectionManager;
    final Gson gson = new Gson();
    public AddCommand(RouteCollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }
    @Override
    public CommandResponse execute(String jsonArgs) {
        DataSource ds = DataSourceProvider.getDataSource();
        if (ds == null) {
            System.out.println("Пустое!!!!");
        }

        try {

            RouteDTO dto = gson.fromJson(jsonArgs, RouteDTO.class);
            Route route = new Route(
                    dto.getKey(),
                    dto.getId(),
                    dto.getName(),
                    dto.getOwner(),
                    new Coordinates(dto.getX(), dto.getY()),
                    new Date(dto.getCreationDate()), // преобразуем long -> Date
                    new Location(dto.getFromX(), dto.getFromY(), dto.getFromZ(), dto.getFromName()),
                    new Location(dto.getToX(), dto.getToY(), dto.getToZ(), dto.getToName())
            );
            System.out.println(route);
            collectionManager.saveToBD(route);
            collectionManager.addToCollection(route);
            return new CommandResponse("Маршрут успешно добавлен!", true);
        } catch (Exception e) {
            //удали
            e.printStackTrace();
            return new CommandResponse("Ошибка добавления маршрута", false);
        }
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "добавляет новый объект в конец коллекции";
    }
}
