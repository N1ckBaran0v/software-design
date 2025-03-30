package traintickets.control.modules;

import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.jdbc.api.JdbcTemplate;
import traintickets.jdbc.api.JdbcTemplateFactory;
import traintickets.jdbc.model.DatabaseParams;

import java.util.Map;

public final class JdbcTemplateModule implements ContextModule {
    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(JdbcTemplate.class, beanProvider -> {
            var factory = beanProvider.getInstance(JdbcTemplateFactory.class);
            var params = new DatabaseParams("jdbc:postgresql://localhost:5432/testtrain1", Map.of("test", "test"), 10);
            return factory.create(params);
        });
    }
}
