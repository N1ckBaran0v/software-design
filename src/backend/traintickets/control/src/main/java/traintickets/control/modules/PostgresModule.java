package traintickets.control.modules;

import traintickets.dataaccess.postgres.factory.PostgresJdbcTemplateFactory;
import traintickets.dataaccess.postgres.repository.*;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.businesslogic.repository.*;
import traintickets.jdbc.api.JdbcTemplateFactory;

import java.util.Map;

public final class PostgresModule implements ContextModule {
    private final Map<String, String> databaseParams;

    public PostgresModule(Map<String, String> databaseParams) {
        this.databaseParams = databaseParams;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        var url = String.format("jdbc:postgresql://%s:%s/%s",
                databaseParams.get("host"), databaseParams.get("port"), databaseParams.get("name"));
        builder.addModule(new JdbcTemplateModule(databaseParams, url))
                .addSingleton(JdbcTemplateFactory.class, PostgresJdbcTemplateFactory.class)
                .addSingleton(CommentRepository.class, CommentRepositoryImpl.class)
                .addSingleton(FilterRepository.class, FilterRepositoryImpl.class)
                .addSingleton(RaceRepository.class, RaceRepositoryImpl.class)
                .addSingleton(RailcarRepository.class, RailcarRepositoryImpl.class)
                .addSingleton(TicketRepository.class, TicketRepositoryImpl.class)
                .addSingleton(TrainRepository.class, TrainRepositoryImpl.class)
                .addSingleton(UserRepository.class, UserRepositoryImpl.class);
    }
}
