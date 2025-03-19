package ru.traintickets.di;

public final class NoSuchBeanException extends RuntimeException {
    NoSuchBeanException(Class<?> clazz) {
        super(String.format("No bean found for class '%s'", clazz.getName()));
    }
}
