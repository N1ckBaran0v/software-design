package traintickets.dataaccess.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import traintickets.dataaccess.factory.PostgresJdbcTemplateFactory;
import traintickets.jdbc.api.JdbcTemplate;
import traintickets.jdbc.api.JdbcTemplateFactory;
import traintickets.jdbc.model.DatabaseParams;

import java.util.Map;

abstract class PostgresIT {
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

    abstract protected void insertData();

    @AfterEach
    void tearDown() {
        container.stop();
        container.close();
    }
}
