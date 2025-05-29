package Commands.Modif;

import Classes.Route;
import Collection.RouteCollectionManager;
import Commands.Command;
import Commands.CommandResponse;
import com.google.gson.Gson;
import sql.DataSourceProvider;

import javax.sql.DataSource;
import java.sql.*;

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
            // Десериализуем объект Route из jsonArgs (например, через Gson)
            Route route = gson.fromJson(jsonArgs, Route.class);
            int id = (int) collectionManager.generateNextId();
            route.setId(id);


            // После добавления - сохранить коллекцию в файл
            collectionManager.saveToFile();

            collectionManager.saveToBD(route);

            return new CommandResponse("Маршрут успешно добавлен!", true);
        } catch (Exception e) {

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
