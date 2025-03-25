package traintickets.jdbc.api;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface TransactionFunction<T> {
    T apply(Connection connection) throws SQLException;
}
