package traintickets.jdbc.impl;

import traintickets.jdbc.exception.UserNotFoundException;

import java.sql.Connection;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

public final class PooledJdbcTemplate extends AbstractJdbcTemplate {
    private final Map<String, ConnectionPool> pools;

    public PooledJdbcTemplate(Driver driver, String url, Map<String, String> users, int poolSize) {
        this.pools = new HashMap<>();
        users.forEach((user, password) -> {
            var props = new Properties();
            props.setProperty("user", user);
            props.setProperty("password", password);
            var provider = (Callable<Connection>) () -> driver.connect(url, props);
            var pool = new ConnectionPool(provider, poolSize);
            pools.put(user, pool);
        });
    }

    @Override
    protected Connection getConnection(String user) {
        var pool = pools.get(user);
        if (pool == null) {
            throw new UserNotFoundException(user);
        }
        return pool.getConnection();
    }

    @Override
    protected void releaseConnection(String user, Connection connection) {
        pools.get(user).releaseConnection(connection);
    }

    @Override
    public void close() {
        pools.values().forEach(ConnectionPool::close);
    }
}
