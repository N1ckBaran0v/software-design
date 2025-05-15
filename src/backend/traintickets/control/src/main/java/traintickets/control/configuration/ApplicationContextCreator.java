package traintickets.control.configuration;

import traintickets.control.modules.*;
import traintickets.di.ApplicationContext;

public final class ApplicationContextCreator {
    private ApplicationContextCreator() {
    }

    public static ApplicationContext create() {
        var appParams = ConfigParser.parseFile("app-settings.yaml");
        var builder = ApplicationContext.builder();
        builder.addModule(new BusinessLogicModule(appParams.getSecurity()));
        var databaseParams = appParams.getDatabase();
        switch (databaseParams.getType()) {
            case "postgresql":
                builder.addModule(new JdbcTemplateModule(databaseParams));
                builder.addModule(new DataAccessPostgresModule());
                break;
            default:
                throw new IllegalArgumentException("Database type not supported: " + databaseParams.getType());
        }
        builder.addModule(new LoggerModule(appParams.getLog()));
        builder.addModule(new PaymentModule());
        builder.addModule(new SecurityModule(appParams.getSecurity(), appParams.getRedis()));
        builder.addModule(new UserInterfaceModule(appParams.getServer(), appParams.getSecurity()));
        return builder.build();
    }
}
