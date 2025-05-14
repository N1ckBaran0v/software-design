package traintickets.control.modules;

import traintickets.control.configuration.DatabaseConfig;
import traintickets.control.configuration.SecurityConfig;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.jdbc.api.JdbcTemplate;
import traintickets.jdbc.api.JdbcTemplateFactory;
import traintickets.jdbc.model.DatabaseParams;

import java.util.Map;

public final class JdbcTemplateModule implements ContextModule {
    private final DatabaseConfig databaseConfig;
    private final SecurityConfig securityConfig;

    public JdbcTemplateModule(DatabaseConfig databaseConfig, SecurityConfig securityConfig) {
        this.databaseConfig = databaseConfig;
        this.securityConfig = securityConfig;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(JdbcTemplate.class, beanProvider -> {
            var factory = beanProvider.getInstance(JdbcTemplateFactory.class);
            var params = new DatabaseParams(
                    databaseConfig.getUrl(),
                    databaseConfig.getUsername(),
                    databaseConfig.getPassword(),
                    Map.of(
                            securityConfig.getUserRole().getAppName(), securityConfig.getUserRole().getDbName(),
                            securityConfig.getCarrierRole().getAppName(), securityConfig.getCarrierRole().getDbName(),
                            securityConfig.getAdminRole().getAppName(), securityConfig.getAdminRole().getDbName(),
                            securityConfig.getSystemRole().getAppName(), securityConfig.getSystemRole().getDbName()
                    ),
                    databaseConfig.getPoolSize()
            );
            return factory.create(params);
        });
    }
}
