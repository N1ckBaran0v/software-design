package ru.traintickets.control.configuration;

import ru.traintickets.control.modules.BusinessLogicModule;
import ru.traintickets.control.modules.DataAccessModule;
import ru.traintickets.control.modules.SecurityModule;
import ru.traintickets.di.ApplicationContext;

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
