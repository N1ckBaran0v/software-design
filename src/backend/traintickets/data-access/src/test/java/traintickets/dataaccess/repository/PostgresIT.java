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
    protected static final String superuser = "superuser";
    protected static final String userRole = "user_role";
    protected static final String carrierRole = "carrier_role";
    protected static final String adminRole = "admin_role";
    protected static final String systemRole = "system_role";

    @BeforeEach
    @SuppressWarnings("all")
    void setUp() {
        container = new PostgreSQLContainer<>("postgres:16.8")
                .withUsername("test")
                .withPassword("test")
                .withInitScripts("schema.sql", "restrictions.sql", "trigger.sql", "roles.sql")
                .withDatabaseName("test");
        container.start();
        var rolesMap = Map.of(
                superuser, container.getUsername(),
                userRole, "_user_role",
                carrierRole, "_carrier_role",
                adminRole, "_admin_role",
                systemRole, "_system_role"
        );
        var params = new DatabaseParams(
                container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword(),
                rolesMap, 1
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
