package traintickets.logger;

import org.slf4j.LoggerFactory;
import org.slf4j.simple.SimpleLogger;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;

public final class UniLoggerFactoryImpl implements UniLoggerFactory {
    public UniLoggerFactoryImpl(String loggingLevel) {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, loggingLevel);
    }

    @Override
    public UniLogger getLogger(Class<?> clazz) {
        return new UniLoggerImpl(LoggerFactory.getLogger(clazz));
    }
}
