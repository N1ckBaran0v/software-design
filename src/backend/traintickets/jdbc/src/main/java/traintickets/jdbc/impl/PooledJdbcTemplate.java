package traintickets.jdbc.impl;

import traintickets.jdbc.model.DatabaseParams;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

public final class PooledJdbcTemplate extends AbstractJdbcTemplate {
    private final ConnectionPool connectionPool;

    public PooledJdbcTemplate(Driver driver, DatabaseParams params) {
        var properties = new Properties();
        properties.setProperty("user", params.username());
        properties.setProperty("password", params.password());
        this.connectionPool = new ConnectionPool(() -> driver.connect(params.url(), properties), params.poolSize());
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    protected Connection getConnection() {
        return connectionPool.getConnection();
    }

    @Override
    protected void releaseConnection(Connection connection) {
        connectionPool.releaseConnection(connection);
    }

    @Override
    public void close() {
        connectionPool.close();
    }
}
