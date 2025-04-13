package traintickets.control.modules;

import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.logger.UniLoggerFactoryImpl;

public final class LoggerModule implements ContextModule {
    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(UniLoggerFactory.class, UniLoggerFactoryImpl.class);
    }
}
