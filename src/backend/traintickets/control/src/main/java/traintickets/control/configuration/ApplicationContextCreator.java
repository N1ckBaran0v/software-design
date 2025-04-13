package traintickets.control.configuration;

import traintickets.control.modules.*;
import traintickets.di.ApplicationContext;

public final class ApplicationContextCreator {
    private ApplicationContextCreator() {
    }

    public static ApplicationContext create(String[] args) {
        var appParams = ConfigParser.parseFile("app-settings.yaml");
        return ApplicationContext.builder()
                .addModule(new BusinessLogicModule(appParams.getSecurity()))
                .addModule(new DataAccessModule())
                .addModule(new JdbcTemplateModule(appParams.getDatabase()))
                .addModule(new LoggerModule())
                .addModule(new PaymentModule())
                .addModule(new SecurityModule(appParams.getRedis()))
                .addModule(new UserInterfaceModule(appParams.getServer(), appParams.getSecurity()))
                .build();
    }
}
