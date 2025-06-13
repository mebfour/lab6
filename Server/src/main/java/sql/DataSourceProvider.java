package sql;

/**
 * Класс для управления соединениями с БД
 */
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceProvider {
    private static HikariDataSource ds;
        static {
        HikariConfig config = new HikariConfig();
        //config.setJdbcUrl("jdbc:postgresql://localhost:5432/studs");
        config.setJdbcUrl("jdbc:postgresql://localhost:51234/studs");
        config.setUsername("s465842");
        config.setPassword("jxFxdy8nXnbewTuG");
        config.setMaximumPoolSize(10); // не больше 10 подключений к бд
        config.setInitializationFailTimeout(-1);    // ждем если не можем подключиться
        ds = new HikariDataSource(config); // через ds приложение будет получать соединения к БД
    }

    public static DataSource getDataSource() {
        return ds;
    }
}

