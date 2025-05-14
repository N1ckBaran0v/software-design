package traintickets.jdbc.impl;

import traintickets.jdbc.api.JdbcTemplate;
import traintickets.jdbc.api.TransactionConsumer;
import traintickets.jdbc.api.TransactionFunction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractJdbcTemplate implements JdbcTemplate {
    private final Map<String, String> roles;

    protected AbstractJdbcTemplate(Map<String, String> roles) {
        this.roles = roles;
    }

    protected abstract Connection getConnection(String user) throws SQLException;
    protected abstract void releaseConnection(String user, Connection connection) throws SQLException;

    @Override
    public <T> T executeFunc(String role, int isolation, TransactionFunction<T> function) {
        Objects.requireNonNull(role);
        Objects.requireNonNull(function);
        try {
            var connection = getConnection(role);
            try {
                configureTransaction(connection, role, isolation);
                var result = function.apply(connection);
                connection.commit();
                return result;
            } catch (Error | Exception e) {
                connection.rollback();
                throw e;
            } finally {
                releaseConnection(role, connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeCons(String role, int isolation, TransactionConsumer consumer) {
        Objects.requireNonNull(role);
        Objects.requireNonNull(consumer);
        try {
            var connection = getConnection(role);
            try {
                configureTransaction(connection, role, isolation);
                consumer.accept(connection);
                connection.commit();
            } catch (Error | Exception e) {
                connection.rollback();
                throw e;
            } finally {
                releaseConnection(role, connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("all")
    private void configureTransaction(Connection connection, String role, int isolation) throws SQLException {
        connection.setTransactionIsolation(isolation);
        try (var statement = connection.prepareStatement("SET ROLE \"" + roles.get(role) + "\";" )) {
            statement.execute();
        }
    }
}
