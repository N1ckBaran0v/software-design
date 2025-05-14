package traintickets.dataaccess.factory;

import traintickets.jdbc.api.JdbcTemplate;
import traintickets.jdbc.api.JdbcTemplateFactory;
import traintickets.jdbc.impl.PooledJdbcTemplate;
import traintickets.jdbc.model.DatabaseParams;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public final class PostgresJdbcTemplateFactory implements JdbcTemplateFactory {
    private static final Driver POSTGRES_DRIVER = loadDriver();

    private static Driver loadDriver() {
        try {
            if (!org.postgresql.Driver.isRegistered()) {
                org.postgresql.Driver.register();
            }
            return DriverManager.getDriver("jdbc:postgresql://");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JdbcTemplate create(DatabaseParams params) {
        Objects.requireNonNull(params);
        return new PooledJdbcTemplate(POSTGRES_DRIVER, params);
    }
}
