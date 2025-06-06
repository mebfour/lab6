package InputHandler;

import Classes.Route;
import sql.DataSourceProvider;
import users.PasswordUtil;

import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RouteRequest {
    private String username;
    private String password;
    private Route route;

    public static boolean isAuthorized(String username, String password) throws SQLException, NoSuchAlgorithmException {
        DataSource ds = DataSourceProvider.getDataSource();
        try (Connection conn = ds.getConnection()) {
            String sql = "SELECT password_hash FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;
                    String storedHash = rs.getString("password_hash");
                    String providedHash = PasswordUtil.hashPassword(password);
                    return storedHash.equals(providedHash);
                }
            }
        }
    }




    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



}
