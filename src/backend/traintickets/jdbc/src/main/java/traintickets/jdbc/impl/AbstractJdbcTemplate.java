package traintickets.jdbc.impl;

import traintickets.jdbc.api.JdbcTemplate;
import traintickets.jdbc.api.TransactionConsumer;
import traintickets.jdbc.api.TransactionFunction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public abstract class AbstractJdbcTemplate implements JdbcTemplate {
    protected abstract Connection getConnection() throws SQLException;
    protected abstract void releaseConnection(Connection connection) throws SQLException;

    @Override
    @SuppressWarnings("all")
    public <T> T executeFunc(int isolation, TransactionFunction<T> function) {
        Objects.requireNonNull(function);
        try {
            var connection = getConnection();
            try {
                connection.setTransactionIsolation(isolation);
                var result = function.apply(connection);
                connection.commit();
                return result;
            } catch (Error | Exception e) {
                connection.rollback();
                throw e;
            } finally {
                releaseConnection(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("all")
    public void executeCons(int isolation, TransactionConsumer consumer) {
        Objects.requireNonNull(consumer);
        try {
            var connection = getConnection();
            try {
                connection.setTransactionIsolation(isolation);
                consumer.accept(connection);
                connection.commit();
            } catch (Error | Exception e) {
                connection.rollback();
                throw e;
            } finally {
                releaseConnection(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
