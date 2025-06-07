package Commands.BDProcessing;

import Classes.Coordinates;
import Classes.Location;
import Classes.Route;
import Collection.RouteCollectionManager;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import sql.DataSourceProvider;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedHashMap;

import static Collection.RouteCollectionManager.globalFilePath;


public class BDReader {

    public static RouteWrapper readRoutesFromBd(String filePath){
        try {

            RouteWrapper wrapper = new RouteWrapper();
            LinkedHashMap<String, Route> routes = new LinkedHashMap<>();

            DataSource ds = DataSourceProvider.getDataSource();
            if (ds == null) {
                System.err.println("Ошибка: DataSource не инициализирован");
                return wrapper;
            }

            String sql = "SELECT id, map_key, name, creation_date, coordinates_x, coordinates_y, " +
                    "to_name, to_x, to_y, to_z, from_name, from_x, from_y, from_z, owner " +
                    "FROM routes";

            try (Connection conn = ds.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Route route = new Route();
                    Location to  = new Location();
                    Location from = new Location();
                    Coordinates coordinates = new Coordinates();

                    coordinates.setX(rs.getLong("coordinates_x"));
                    coordinates.setY(rs.getInt("coordinates_y"));
                    to.setName(rs.getString("to_name"));
                    to.setX(rs.getLong("to_x"));
                    to.setY(rs.getInt("to_y"));
                    to.setZ(rs.getInt("to_z"));
                    from.setName(rs.getString("from_name"));
                    from.setX(rs.getLong("from_x"));
                    from.setY(rs.getInt("from_y"));
                    from.setZ(rs.getInt("from_z"));

                    route.setId(rs.getInt("id"));
                    route.setName(rs.getString("name"));
                    route.setCoordinates(coordinates);
                    java.sql.Timestamp timestamp = rs.getTimestamp("creation_date");
                    if (timestamp != null) route.setCreationDate(new Date(timestamp.getTime()));
                    else route.setCreationDate(null);

                    route.setTo(to);
                    route.setFrom(from);
                    route.setKey(rs.getString("map_key"));
                    route.setOwner(rs.getString("owner"));

                    routes.put(route.getKey(), route);
                }

                wrapper.setRouteMap(routes);

                // Устанавливаем время инициализации как текущее время


            } catch (Exception e) {
                System.err.println("Ошибка при загрузке коллекции из базы данных");
                //удали
                e.printStackTrace();
            }


            return wrapper;
        } catch (Exception e) {

            System.err.println("Ошибка при чтении из XML");

            return new RouteWrapper();
        }
    }
    private static LinkedHashMap<String, Route> readDefaultFile() {
        try {
            File defaultFile = new File(globalFilePath);
            if (defaultFile.exists()) {
                try (InputStream is = new FileInputStream(defaultFile)) {
                    JAXBContext context = JAXBContext.newInstance(RouteWrapper.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    RouteWrapper wrapper = (RouteWrapper) unmarshaller.unmarshal(is);
                    if (wrapper.getInitializationTime()!= null){
                        RouteCollectionManager.setInitializationTime(wrapper.getInitializationTime());
                    }
                    return wrapper.getRouteMap() != null ?
                            new LinkedHashMap<>(wrapper.getRouteMap()) :
                            new LinkedHashMap<>();
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка чтения файла по умолчанию");
        }
        return new LinkedHashMap<>(); // Возвращаем пустую коллекцию
    }
}

