package sql;



public class DatabaseManager {
    static {
        try {
            Class.forName("org.postgresql.Driver"); // регистрация драйвера
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка: Драйвер PostgreSQL не найден.");
        }
    }





}

