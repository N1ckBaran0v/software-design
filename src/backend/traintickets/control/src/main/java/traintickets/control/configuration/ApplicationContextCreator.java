package traintickets.control.configuration;

import traintickets.control.modules.*;
import traintickets.di.ApplicationContext;

public final class ApplicationContextCreator {
    private ApplicationContextCreator() {
    }

    public static ApplicationContext create(String[] args) {
        return ApplicationContext.builder()
                .addModule(new BusinessLogicModule())
                .addModule(new DataAccessModule())
                .addModule(new JdbcTemplateModule())
                .addModule(new PaymentModule())
                .addModule(new SecurityModule())
                .build();
    }
}
