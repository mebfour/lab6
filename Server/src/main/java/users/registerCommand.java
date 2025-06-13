package users;


import Commands.Command;
import Commands.CommandResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import sql.DataSourceProvider;

import javax.sql.*;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Map;

public class RegisterCommand implements Command {
    final Gson gson = new Gson();
    LoginCommand loginCommand = new LoginCommand();
    @Override
    public CommandResponse execute(String jsonArgs) {
        DataSource ds = DataSourceProvider.getDataSource();
        if (ds == null) {
            return new CommandResponse("Ошибка подключения к базе данных", false);
        }

        try (Connection conn = DataSourceProvider.getDataSource().getConnection()) {
            // Десериализуем параметры регистрации из JSON
            Map<String, String> params = gson.fromJson(jsonArgs, new TypeToken<Map<String, String>>(){}.getType());
            String username = params.get("username");
            String password = params.get("password");
            System.out.println("регаем");
            if (username == null || password == null || username.isBlank() || password.isBlank()) {
                return new CommandResponse("Логин и пароль не могут быть пустыми", false);
            }

            // Проверяем, существует ли пользователь
            if (userExists(conn, username)) {
                return new CommandResponse("Пользователь с таким логином уже существует", false);
            }

            // Хэшируем пароль
            String hashedPassword = password;

            // Сохраняем пользователя в базу
            String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, hashedPassword);
                ps.executeUpdate();
            }
            loginCommand.setUsername(username);
            return new CommandResponse("Регистрация прошла успешно", true);

        } catch (SQLException e) {
            return new CommandResponse("Ошибка работы с базой данных: " + e.getMessage(), false);
        }
    }

    private boolean userExists(Connection conn, String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "регистрирует нового пользователя";
    }
}
