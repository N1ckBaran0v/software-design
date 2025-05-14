package traintickets.control.modules;

import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.control.configuration.LogConfig;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.logger.UniLoggerFactoryImpl;

public final class LoggerModule implements ContextModule {
    private final LogConfig logConfig;

    public LoggerModule(LogConfig logConfig) {
        this.logConfig = logConfig;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(UniLoggerFactory.class, beanProvider -> new UniLoggerFactoryImpl(logConfig.getLevel()));
    }
}
