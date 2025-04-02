package traintickets.jdbc.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

final class ConnectionPool {
    private final List<Connection> connections;
    private final Semaphore semaphore;

    ConnectionPool(Callable<Connection> provider, int poolSize) {
        try {
            connections = new ArrayList<>(poolSize);
            for (var i = 0; i < poolSize; ++i) {
                var connection = provider.call();
                connection.setAutoCommit(false);
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
            synchronized (connections) {
                return connections.removeLast();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void releaseConnection(Connection connection) {
        synchronized (connections) {
            connections.add(connection);
            semaphore.release();
        }
    }
}
