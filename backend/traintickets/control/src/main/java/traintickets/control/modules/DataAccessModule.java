package traintickets.control.modules;

import traintickets.businesslogic.payment.PaymentManager;
import traintickets.dataaccess.factory.PostgresJdbcTemplateFactory;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.businesslogic.repository.*;
import traintickets.dataaccess.repository.*;
import traintickets.jdbc.api.JdbcTemplate;
import traintickets.jdbc.api.JdbcTemplateFactory;

public final class DataAccessModule implements ContextModule {
    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(JdbcTemplateFactory.class, PostgresJdbcTemplateFactory.class)
                .addSingleton(CommentRepository.class, beanProvider -> {
                    var jdbcTemplate = beanProvider.getInstance(JdbcTemplate.class);
                    return new CommentRepositoryImpl(jdbcTemplate, "test");
                })
                .addSingleton(FilterRepository.class, beanProvider -> {
                    var jdbcTemplate = beanProvider.getInstance(JdbcTemplate.class);
                    return new FilterRepositoryImpl(jdbcTemplate, "test");
                })
                .addSingleton(RaceRepository.class, beanProvider -> {
                    var jdbcTemplate = beanProvider.getInstance(JdbcTemplate.class);
                    return new RaceRepositoryImpl(jdbcTemplate, "test");
                })
                .addSingleton(RailcarRepository.class, beanProvider -> {
                    var jdbcTemplate = beanProvider.getInstance(JdbcTemplate.class);
                    return new RailcarRepositoryImpl(jdbcTemplate, "test");
                })
                .addSingleton(TicketRepository.class, beanProvider -> {
                    var jdbcTemplate = beanProvider.getInstance(JdbcTemplate.class);
                    var manager = beanProvider.getInstance(PaymentManager.class);
                    return new TicketRepositoryImpl(jdbcTemplate, manager, "test");
                })
                .addSingleton(TrainRepository.class, beanProvider -> {
                    var jdbcTemplate = beanProvider.getInstance(JdbcTemplate.class);
                    return new TrainRepositoryImpl(jdbcTemplate, "test");
                })
                .addSingleton(UserRepository.class, beanProvider -> {
                    var jdbcTemplate = beanProvider.getInstance(JdbcTemplate.class);
                    return new UserRepositoryImpl(jdbcTemplate, "test");
                });
    }
}
