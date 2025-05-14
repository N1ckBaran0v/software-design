package traintickets.jdbc.impl;

import traintickets.jdbc.model.DatabaseParams;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

public final class PooledJdbcTemplate extends AbstractJdbcTemplate {
    private final ConnectionPool connectionPool;

    public PooledJdbcTemplate(Driver driver, DatabaseParams params) {
        super(params.roles());
        var properties = new Properties();
        properties.setProperty("user", params.username());
        properties.setProperty("password", params.password());
        this.connectionPool = new ConnectionPool(() -> driver.connect(params.url(), properties), params.poolSize());
    }

    @Override
    protected Connection getConnection(String user) {
        return connectionPool.getConnection();
    }

    @Override
    protected void releaseConnection(String user, Connection connection) {
        connectionPool.releaseConnection(connection);
    }

    @Override
    public void close() {
        connectionPool.close();
    }
}
