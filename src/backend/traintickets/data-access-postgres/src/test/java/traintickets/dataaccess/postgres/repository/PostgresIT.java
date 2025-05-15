package traintickets.dataaccess.postgres.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import traintickets.dataaccess.postgres.factory.PostgresJdbcTemplateFactory;
import traintickets.jdbc.api.JdbcTemplate;
import traintickets.jdbc.api.JdbcTemplateFactory;
import traintickets.jdbc.model.DatabaseParams;

abstract class PostgresIT {
    private static final JdbcTemplateFactory jdbcTemplateFactory = new PostgresJdbcTemplateFactory();

    protected PostgreSQLContainer<?> container;
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    @SuppressWarnings("all")
    void setUp() {
        container = new PostgreSQLContainer<>("postgres:16.8")
                .withUsername("test")
                .withPassword("test")
                .withInitScripts("schema.sql", "restrictions.sql", "trigger.sql")
                .withDatabaseName("test");
        container.start();
        var params = new DatabaseParams(
                container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword(),
                1
        );
        jdbcTemplate = jdbcTemplateFactory.create(params);
        insertData();
    }

    abstract protected void insertData();

    @AfterEach
    void tearDown() {
        container.stop();
        container.close();
    }
}
