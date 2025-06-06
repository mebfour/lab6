package Collection;

import Classes.Route;

import java.io.File;
import java.sql.*;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Класс для управления коллекцией маршрутов.
 * Инкапсулирует логику работы с данными.
 */

import Commands.XmlProcessing.RouteWrapper;
import Commands.XmlProcessing.XmlRouteReader;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import sql.DataSourceProvider;

import static managers.CommandManager.collectionManager;

@XmlRootElement(name = "routeList")
public class RouteCollectionManager {
    public static String globalFilePath = "file.xml";
    public static LinkedHashMap<String, Route> routeList = XmlRouteReader.readRoutesFromBd(globalFilePath).getRouteMap();
    private static Date initializationTime = new Date();
    private int currentMaxId = (int) routeList.values().stream()
                .mapToLong(Route::getId)
                .max()
                .orElse(0);
    public synchronized long generateNextId() {
        return ++currentMaxId;
    }

    public static void init(String path) {
        globalFilePath = path;
        routeList = XmlRouteReader.readRoutesFromBd(globalFilePath).getRouteMap();

    }

    public static Date getInitializationTime() {
        return initializationTime;
    }

    public static void setInitializationTime(Date date) {
        if (date != null) {
            initializationTime = date;
        }
    }

    public void addToCollection(Route route){
        try {
            routeList.put(route.getKey(), route);
        }catch (Exception e){
            routeList.put(route.getName(),route);
        }

    }

    
    public static Class<?> getCollectionType(){
        return routeList.getClass();
    }

    @XmlElement(name="route")
    public LinkedHashMap getCollection() {
        return routeList;
    }

    public void updateToBD(Route route) {
        String sql = "UPDATE routes\n" +
                "SET map_key = ?,\n" +
                "    name = ?,\n" +
                "    creation_date = ?,\n" +
                "    coordinates_x = ?,\n" +
                "    coordinates_y = ?,\n" +
                "    to_name = ?,\n" +
                "    to_x = ?,\n" +
                "    to_y = ?,\n" +
                "    to_z = ?,\n" +
                "    from_name = ?,\n" +
                "    from_x = ?,\n" +
                "    from_y = ?,\n" +
                "    from_z = ?\n" +
                "WHERE id = ?\n" +
                "RETURNING id;\n";

        //  Сохраняем в БД

        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){


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
            ps.setInt(14, route.getId());
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    int id = rs.getInt("id");
                    System.out.println("с id элемент обновлен");
                }else {
                    System.out.println("Элемент не обновлен");
                }
            }



            // Добавляем в коллекцию
            collectionManager.addToCollection(route);
        }catch (SQLException e) {
            System.err.println("Ошибка подключения к БД");

        }
    }
    public void removeConcrFromBD(String key) {
        System.out.println(key);
        String sql = "DELETE FROM routes WHERE map_key = ?";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {


            System.out.println("Удаляем запись с key: " + key);
            ps.setString(1, key); // Устанавливаем значение key для удаления

            int rowsDeleted = ps.executeUpdate(); // Выполняем запрос на удаление
            System.out.println("Количество удалённых записей: " + rowsDeleted);

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении с БД");

        }
    }


    public void clearToBD() {
        String sql = "DELETE FROM routes";
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int rowsDeleted = ps.executeUpdate(); // Выполняем запрос на удаление
            System.out.println("Удалено записей: " + rowsDeleted);

        } catch (SQLException e) {
            System.err.println("Ошибка работы с БД: " + e.getMessage());
        }
    }


    public void saveToBD(Route route){
        String sql = "INSERT INTO routes (map_key, name, creation_date, coordinates_x, coordinates_y, " +
                "to_name, to_x, to_y, to_z, from_name, from_x, from_y, from_z) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        //  Сохраняем в БД
        try (Connection conn = DataSourceProvider.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){


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
            // Добавляем в коллекцию
            collectionManager.addToCollection(route);
        }catch (SQLException e) {
            System.err.println("Ошибка подключения  к БД");
        }
    }


    public void saveToFile() {
        String filePath = globalFilePath;
        try {
            RouteWrapper wrapper = new RouteWrapper();
            wrapper.setRouteMap(routeList);
            wrapper.setInitializationTime(initializationTime);

            JAXBContext context = JAXBContext.newInstance(RouteWrapper.class, Route.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(wrapper, new File(filePath));
        } catch (Exception e) {

            System.err.println("Ошибка при сохранении в XML");
        }

    }
}
