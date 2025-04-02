package traintickets.jdbc.impl;

import traintickets.jdbc.api.JdbcTemplate;
import traintickets.jdbc.api.TransactionConsumer;
import traintickets.jdbc.api.TransactionFunction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public abstract class AbstractJdbcTemplate implements JdbcTemplate {
    protected abstract Connection getConnection(String user) throws SQLException;
    protected abstract void releaseConnection(String user, Connection connection) throws SQLException;

    @Override
    public <T> T executeFunc(String username, int isolation, TransactionFunction<T> function) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(function);
        try {
            var connection = getConnection(username);
            try {
                connection.setTransactionIsolation(isolation);
                var result = function.apply(connection);
                connection.commit();
                return result;
            } catch (Error | Exception e) {
                connection.rollback();
                throw e;
            } finally {
                releaseConnection(username, connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeCons(String username, int isolation, TransactionConsumer consumer) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(consumer);
        try {
            var connection = getConnection(username);
            try {
                connection.setTransactionIsolation(isolation);
                consumer.accept(connection);
                connection.commit();
            } catch (Error | Exception e) {
                connection.rollback();
                throw e;
            } finally {
                releaseConnection(username, connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
