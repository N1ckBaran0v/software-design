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
    private final String url;

    public JdbcTemplateModule(DatabaseConfig databaseConfig, String url) {
        this.databaseConfig = databaseConfig;
        this.url = url;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(JdbcTemplate.class, beanProvider -> {
            var factory = beanProvider.getInstance(JdbcTemplateFactory.class);
            var params = new DatabaseParams(
                    url,
                    databaseConfig.getUsername(),
                    databaseConfig.getPassword(),
                    databaseConfig.getPoolSize()
            );
            return factory.create(params);
        });
    }
}
