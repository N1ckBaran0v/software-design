package traintickets.control.modules;

import traintickets.dataaccess.postgres.factory.PostgresJdbcTemplateFactory;
import traintickets.dataaccess.postgres.repository.*;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.businesslogic.repository.*;
import traintickets.jdbc.api.JdbcTemplateFactory;

public final class DataAccessPostgresModule implements ContextModule {
    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(JdbcTemplateFactory.class, PostgresJdbcTemplateFactory.class)
                .addSingleton(CommentRepository.class, CommentRepositoryImpl.class)
                .addSingleton(FilterRepository.class, FilterRepositoryImpl.class)
                .addSingleton(RaceRepository.class, RaceRepositoryImpl.class)
                .addSingleton(RailcarRepository.class, RailcarRepositoryImpl.class)
                .addSingleton(TicketRepository.class, TicketRepositoryImpl.class)
                .addSingleton(TrainRepository.class, TrainRepositoryImpl.class)
                .addSingleton(UserRepository.class, UserRepositoryImpl.class);
    }
}
