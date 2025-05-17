package traintickets.control.modules;

import traintickets.control.configuration.DatabaseConfig;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;

public final class DatabaseModule implements ContextModule {
    private final DatabaseConfig databaseConfig;

    public DatabaseModule(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addModule(switch (databaseConfig.getType()) {
            case "postgresql" -> new PostgresModule(databaseConfig);
            case "mongodb" -> new MongoModule(databaseConfig);
            default -> throw new IllegalStateException("Unsupported database type: " + databaseConfig.getType());
        });
    }
}
