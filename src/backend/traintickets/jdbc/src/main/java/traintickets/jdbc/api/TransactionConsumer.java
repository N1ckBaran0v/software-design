package traintickets.jdbc.api;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface TransactionConsumer {
    void accept(Connection connection) throws SQLException;
}
