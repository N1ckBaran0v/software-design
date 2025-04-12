package traintickets.control.modules;

import traintickets.control.configuration.DatabaseConfig;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.jdbc.api.JdbcTemplate;
import traintickets.jdbc.api.JdbcTemplateFactory;
import traintickets.jdbc.model.DatabaseParams;

public final class JdbcTemplateModule implements ContextModule {
    private final DatabaseConfig databaseConfig;

    public JdbcTemplateModule(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(JdbcTemplate.class, beanProvider -> {
            var factory = beanProvider.getInstance(JdbcTemplateFactory.class);
            var params = new DatabaseParams(
                    databaseConfig.getUrl(), databaseConfig.getUsers(), databaseConfig.getPoolSize());
            return factory.create(params);
        });
    }
}
