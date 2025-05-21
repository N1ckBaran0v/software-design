package traintickets.control.modules;

import traintickets.businesslogic.repository.*;
import traintickets.dataaccess.mongo.connection.MongoConfig;
import traintickets.dataaccess.mongo.connection.MongoExecutor;
import traintickets.dataaccess.mongo.repository.*;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;

import java.util.Map;

public final class MongoModule implements ContextModule {
    private final Map<String, String> databaseParams;

    public MongoModule(Map<String, String> databaseParams) {
        this.databaseParams = databaseParams;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(MongoExecutor.class, beanProvider -> {
                    var mongoConfig = new MongoConfig(
                            databaseParams.get("host"),
                            Integer.parseInt(databaseParams.get("port")),
                            databaseParams.get("name"),
                            databaseParams.get("username"),
                            databaseParams.get("password"),
                            Integer.parseInt(databaseParams.get("poolSize")),
                            databaseParams.get("replicaSet")
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
