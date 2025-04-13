package traintickets.logger;

import org.slf4j.LoggerFactory;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;

public final class UniLoggerFactoryImpl implements UniLoggerFactory {
    @Override
    public UniLogger getLogger(Class<?> clazz) {
        return new UniLoggerImpl(LoggerFactory.getLogger(clazz));
    }
}
