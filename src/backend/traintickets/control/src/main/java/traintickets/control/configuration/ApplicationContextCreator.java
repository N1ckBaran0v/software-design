package traintickets.control.configuration;

import traintickets.control.modules.*;
import traintickets.di.ApplicationContext;

public final class ApplicationContextCreator {
    private ApplicationContextCreator() {
    }

    public static ApplicationContext create() {
        var appParams = ConfigParser.parseFile("app-settings.yaml");
        return ApplicationContext.builder()
                .addModule(new BusinessLogicModule(appParams.getSecurity()))
                .addModule(new DataAccessModule())
                .addModule(new JdbcTemplateModule(appParams.getDatabase()))
                .addModule(new LoggerModule(appParams.getLog()))
                .addModule(new PaymentModule())
                .addModule(new SecurityModule(appParams.getSecurity(), appParams.getRedis()))
                .addModule(new UserInterfaceModule(appParams.getServer(), appParams.getSecurity()))
                .build();
    }
}
