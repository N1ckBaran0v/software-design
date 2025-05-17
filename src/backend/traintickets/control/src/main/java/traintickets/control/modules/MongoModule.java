package traintickets.control.modules;

import traintickets.businesslogic.repository.*;
import traintickets.control.configuration.DatabaseConfig;
import traintickets.dataaccess.mongo.connection.MongoConfig;
import traintickets.dataaccess.mongo.connection.MongoExecutor;
import traintickets.dataaccess.mongo.repository.*;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;

public final class MongoModule implements ContextModule {
    private final DatabaseConfig databaseConfig;

    public MongoModule(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(MongoExecutor.class, beanProvider -> {
                    var mongoConfig = new MongoConfig(
                            databaseConfig.getHost(),
                            databaseConfig.getPort(),
                            databaseConfig.getName(),
                            databaseConfig.getUsername(),
                            databaseConfig.getPassword(),
                            databaseConfig.getPort()
                    );
                    return new MongoExecutor(mongoConfig);
                })
                .addSingleton(CommentRepository.class, CommentRepositoryImpl.class)
                .addSingleton(FilterRepository.class, FilterRepositoryImpl.class)
                .addSingleton(RaceRepository.class, RaceRepositoryImpl.class)
                .addSingleton(RailcarRepository.class, RailcarRepositoryImpl.class)
                .addSingleton(TicketRepository.class, TicketRepositoryImpl.class)
                .addSingleton(TrainRepository.class, TrainRepositoryImpl.class)
                .addSingleton(UserRepository.class, UserRepositoryImpl.class);
    }
}
