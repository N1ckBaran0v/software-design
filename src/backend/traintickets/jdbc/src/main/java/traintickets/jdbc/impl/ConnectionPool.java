package traintickets.jdbc.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

final class ConnectionPool implements AutoCloseable {
    private final List<Connection> connections;
    private final List<Connection> pool;
    private final Semaphore semaphore;

    ConnectionPool(Callable<Connection> provider, int poolSize) {
        try {
            connections = new ArrayList<>(poolSize);
            pool = new ArrayList<>(poolSize);
            for (var i = 0; i < poolSize; ++i) {
                var connection = provider.call();
                connection.setAutoCommit(false);
                pool.add(connection);
                connections.add(connection);
            }
            semaphore = new Semaphore(poolSize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Connection getConnection() {
        try {
            semaphore.acquire();
            synchronized (pool) {
                return pool.removeLast();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void releaseConnection(Connection connection) {
        synchronized (pool) {
            pool.add(connection);
            semaphore.release();
        }
    }

    @Override
    public void close() {
        connections.forEach(connection -> {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
