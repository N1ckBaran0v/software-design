package traintickets.control.configuration;

import traintickets.control.modules.BusinessLogicModule;
import traintickets.control.modules.DataAccessModule;
import traintickets.control.modules.SecurityModule;
import traintickets.di.ApplicationContext;

public final class ApplicationContextCreator {
    private ApplicationContextCreator() {
    }

    public static ApplicationContext create(String[] args) {
        return ApplicationContext.builder()
                .addModule(new BusinessLogicModule())
                .addModule(new DataAccessModule())
                .addModule(new SecurityModule())
                .build();
    }
}
