package sql;

/**
 * Класс для управления соединениями с БД
 */
import com.sun.tools.javac.Main;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataSourceProvider {
    private static HikariDataSource ds;
        static {
            try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
                Properties prop = new Properties();
                prop.load(input);
                HikariConfig config = new HikariConfig();
                //config.setJdbcUrl("jdbc:postgresql://localhost:5432/studs");
                config.setJdbcUrl("jdbc:postgresql://localhost:51234/studs");
                config.setUsername("s465842");
                config.setPassword(prop.getProperty("db.password"));
                config.setMaximumPoolSize(10); // не больше 10 подключений к бд
                config.setInitializationFailTimeout(-1);    // ждем если не можем подключиться
                ds = new HikariDataSource(config); // через ds приложение будет получать соединения к БД
            } catch (IOException ex) {
        throw new RuntimeException("Error loading database configuration", ex);
    }    }

    public static DataSource getDataSource() {
        return ds;
    }
}

