package traintickets.logger;

import org.slf4j.Logger;
import traintickets.businesslogic.logger.UniLogger;

public final class UniLoggerImpl implements UniLogger {
    private Logger logger;

    UniLoggerImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void trace(String message, Object... args) {
        logger.trace(getMessage(message, args));
    }

    @Override
    public void debug(String message, Object... args) {
        logger.debug(getMessage(message, args));
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(getMessage(message, args));
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(getMessage(message, args));
    }

    @Override
    public void error(String message, Object... args) {
        logger.error(getMessage(message, args));
    }

    private String getMessage(String message, Object... args) {
        return String.format(message, args);
    }
}
