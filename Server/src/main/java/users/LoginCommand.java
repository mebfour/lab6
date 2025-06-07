package users;

import Commands.Command;
import Commands.CommandResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import sql.DataSourceProvider;

import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Map;

public class LoginCommand implements Command {
    private final Gson gson = new Gson();
    private static final DataSource ds = DataSourceProvider.getDataSource();
    public static String username;
    @Override
    public CommandResponse execute(String jsonArgs) {
        if (ds == null) {
            return new CommandResponse("Ошибка подключения к базе данных", false);
        }

        try (Connection conn = DataSourceProvider.getDataSource().getConnection()) {
            Map<String, String> params = gson.fromJson(jsonArgs, new TypeToken<Map<String, String>>(){}.getType());
            username = params.get("username");
            String password = params.get("password");

            if (username == null || password == null || username.isBlank() || password.isBlank()) {
                return new CommandResponse("Логин и пароль не могут быть пустыми", false);
            }

            String sql = "SELECT password_hash FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return new CommandResponse("Пользователь не найден", false);
                    }
                    String storedHash = rs.getString("password_hash");
                    String providedHash = PasswordUtil.hashPassword(password);

                    if (storedHash.equals(providedHash)) {
                        return new CommandResponse("Авторизация успешна", true);
                    } else {
                        return new CommandResponse("Неверный пароль", false);
                    }
                }
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            // удали
            e.printStackTrace();
            return new CommandResponse("Ошибка при авторизации: " + e.getMessage(), false);
        }
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        LoginCommand.username = username;
    }

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getDescription() {
        return "позволяет войти";
    }
}

