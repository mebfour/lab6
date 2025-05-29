package sql;

import Classes.Coordinates;
import Classes.Location;
import Classes.Route;
import Collection.RouteCollectionManager;

import java.sql.*;

public class DatabaseManager {
    static {
        try {
            Class.forName("org.postgresql.Driver"); // регистрация драйвера
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка: Драйвер PostgreSQL не найден.");
        }
    }





}

