package traintickets.control.modules;

import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.jdbc.api.JdbcTemplate;
import traintickets.jdbc.api.JdbcTemplateFactory;
import traintickets.jdbc.model.DatabaseParams;

import java.util.Map;

public final class JdbcTemplateModule implements ContextModule {
    private final Map<String, String> databaseParams;
    private final String url;

    public JdbcTemplateModule(Map<String, String> databaseParams, String url) {
        this.databaseParams = databaseParams;
        this.url = url;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(JdbcTemplate.class, beanProvider -> {
            var factory = beanProvider.getInstance(JdbcTemplateFactory.class);
            var params = new DatabaseParams(
                    url,
                    databaseParams.get("username"),
                    databaseParams.get("password"),
                    Integer.parseInt(databaseParams.get("poolSize"))
            );
            return factory.create(params);
        });
    }
}
