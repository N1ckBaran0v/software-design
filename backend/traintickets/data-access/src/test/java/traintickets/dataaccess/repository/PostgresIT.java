package traintickets.dataaccess.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import traintickets.dataaccess.factory.PostgresJdbcTemplateFactory;
import traintickets.jdbc.api.JdbcTemplate;
import traintickets.jdbc.api.JdbcTemplateFactory;
import traintickets.jdbc.model.DatabaseParams;

import java.sql.Connection;
import java.util.Map;

public class PostgresIT {
    private static final JdbcTemplateFactory jdbcTemplateFactory = new PostgresJdbcTemplateFactory();

    protected PostgreSQLContainer<?> container;
    protected JdbcTemplate jdbcTemplate;
    protected static final String roleName = "test";

    @BeforeEach
    @SuppressWarnings("all")
    void setUp() {
        container = new PostgreSQLContainer<>("postgres:16")
                .withUsername("test")
                .withPassword("test")
                .withInitScript("schema.sql")
                .withDatabaseName("test");
        container.start();
        var params = new DatabaseParams(container.getJdbcUrl(), Map.of("test", "test"), 1);
        jdbcTemplate = jdbcTemplateFactory.create(params);
        insertData();
    }

    private void insertData() {
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "insert into users_view (user_name, pass_word, real_name, user_role, is_active) " +
                            "values ('first', 'qwerty123', 'Иванов Иван Иванович', 'userRole', TRUE); " +
                            "insert into users_view (user_name, pass_word, real_name, user_role, is_active) " +
                            "values ('second', 'qwerty123', 'Петров Пётр Петрович', 'userRole', TRUE); " +
                            "insert into trains (train_class) values ('Скорый'); " +
                            "insert into comments (user_id, train_id, score, comment_text) " +
                            "values (1, 1, 5, 'Лучший поезд'); " +
                            "insert into comments (user_id, train_id, score, comment_text) " +
                            "values (2, 1, 1, 'Грубые проводники'); " +
                            "insert into filters (user_id, filter_name, departure, destination, train_class, transfers, min_cost, max_cost) " +
                            "values (1, 'first', 'first', 'second', 'Экспресс', 0, 100, 10000); " +
                            "insert into filters (user_id, filter_name, departure, destination, train_class, transfers, min_cost, max_cost) " +
                            "values (1, 'second', 'first', 'second', 'Скорый', 1, 100, 10000); " +
                            "insert into passengers (filter_id, passengers_type, passengers_count) " +
                            "values (1, 'adult', 2); " +
                            "insert into passengers (filter_id, passengers_type, passengers_count) " +
                            "values (1, 'child', 1); " +
                            "insert into passengers (filter_id, passengers_type, passengers_count) " +
                            "values (2, 'adult', 1); "
            )) {
                statement.execute();
            }
        });
    }

    @AfterEach
    void tearDown() {
        container.stop();
        container.close();
    }
}
