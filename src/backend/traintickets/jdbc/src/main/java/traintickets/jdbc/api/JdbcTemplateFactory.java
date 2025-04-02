package traintickets.jdbc.api;

import traintickets.jdbc.model.DatabaseParams;

public interface JdbcTemplateFactory {
    JdbcTemplate create(DatabaseParams params);
}
