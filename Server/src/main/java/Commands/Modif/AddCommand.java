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
            // Добавляем в коллекцию
            collectionManager.addToCollection(route);

            // После добавления - сохранить коллекцию в файл
            collectionManager.saveToFile();

            String sql = "INSERT INTO routes (map_key, name, creation_date, coordinates_x, coordinates_y, " +
                    "to_name, to_x, to_y, to_z, from_name, from_x, from_y, from_z) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

            //  Сохраняем в БД
            System.out.println("Удыл мы тут перед бд" );
            try (Connection conn = DataSourceProvider.getDataSource().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)){

                System.out.println("Удыл мы тут внутри бд" );
                    ps.setString(1, route.getKey());
                    ps.setString(2, route.getName());
                    ps.setTimestamp(3, Timestamp.from(route.getCreationDate().toInstant()));
                    ps.setLong(4, route.getCoordinates().getX());
                    ps.setInt(5, route.getCoordinates().getY());
                    ps.setString(6, route.getTo().getName());
                    ps.setFloat(7, route.getTo().getX());
                    ps.setInt(8, route.getTo().getY());
                    ps.setInt(9, route.getTo().getZ());
                    ps.setString(10, route.getFrom().getName());
                    ps.setFloat(11, route.getFrom().getX());
                    ps.setInt(12, route.getFrom().getY());
                    ps.setInt(13, route.getFrom().getZ());

                    try(ResultSet rs = ps.executeQuery()){
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        route.setId(generatedId); // id, сгенерированный БД
                    }
                }
            }catch (SQLException e) {
                System.err.println("Ошибка подключения");
            }

            return new CommandResponse("Маршрут успешно добавлен!", true);
        } catch (Exception e) {
            //  удали
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
