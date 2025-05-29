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
        config.setJdbcUrl("jdbc:postgresql://localhost:51234/studs");
        config.setUsername("s465842");
        config.setPassword("jxFxdy8nXnbewTuG");
        config.setMaximumPoolSize(10); // настройте по нагрузке

        ds = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return ds;
    }
}

