package traintickets.di;

public final class MultipleSubclassesDetectedException extends RuntimeException {
    MultipleSubclassesDetectedException(Class<?> clazz) {
        super(String.format("Multiple subclasses found for class '%s'.", clazz.getName()));
    }
}
