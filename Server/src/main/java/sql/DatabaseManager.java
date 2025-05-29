package sql;

import Classes.Route;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://pg/studs";
    private static final String USER = "s465842";      // заменить на реальные
    private static final String PASSWORD = "QInJ+4603";  // заменить на реальные

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Успешное подключение к базе данных!");
            } else {
                System.out.println("Не удалось подключиться к базе данных.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        }
    }

    static {
        try {
            Class.forName("org.postgresql.Driver"); // регистрация драйвера
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка: Драйвер PostgreSQL не найден.");
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Пример метода для запроса
    public Route getRouteById(int id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM routes WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Создайте объект Route из результата
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка выполнения SQL-запроса");
        }
        return null;
    }
}

