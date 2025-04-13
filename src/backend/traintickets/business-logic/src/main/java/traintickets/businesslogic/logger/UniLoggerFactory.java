package traintickets.businesslogic.logger;

public interface UniLoggerFactory {
    UniLogger getLogger(Class<?> clazz);
}
