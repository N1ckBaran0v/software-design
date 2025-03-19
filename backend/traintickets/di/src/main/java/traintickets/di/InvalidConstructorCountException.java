package traintickets.di;

public final class InvalidConstructorCountException extends RuntimeException {
    InvalidConstructorCountException(Class<?> clazz, int count) {
        super(String.format("Class '%s' has %d constructors (1 expected)", clazz.getName(), count));
    }
}
