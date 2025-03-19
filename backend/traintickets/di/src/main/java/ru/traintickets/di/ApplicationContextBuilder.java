package ru.traintickets.di;

import java.util.Objects;

public final class ApplicationContextBuilder {
    private final BeanCollector beanCollector;

    ApplicationContextBuilder() {
        this.beanCollector = new BeanCollector();
    }

    public ApplicationContext build() {
        return new ApplicationContext(beanCollector);
    }

    public <T> ApplicationContextBuilder addSingleton(Class<T> clazz, Class<? extends T> subclass) {
        beanCollector.addSingleton(Objects.requireNonNull(clazz), Objects.requireNonNull(subclass));
        return this;
    }

    public <T> ApplicationContextBuilder addPrototype(Class<T> clazz, Class<? extends T> subclass) {
        beanCollector.addPrototype(Objects.requireNonNull(clazz), Objects.requireNonNull(subclass));
        return this;
    }

    public <T> ApplicationContextBuilder addSingleton(Class<T> clazz, BeanCreator<T> creator) {
        beanCollector.addSingleton(Objects.requireNonNull(clazz), Objects.requireNonNull(creator));
        return this;
    }

    public <T> ApplicationContextBuilder addPrototype(Class<T> clazz, BeanCreator<T> creator) {
        beanCollector.addPrototype(Objects.requireNonNull(clazz), Objects.requireNonNull(creator));
        return this;
    }

    public <T> ApplicationContextBuilder addInstance(Class<T> clazz, T instance) {
        beanCollector.addInstance(Objects.requireNonNull(clazz), Objects.requireNonNull(instance));
        return this;
    }

    public ApplicationContextBuilder addModule(ContextModule module) {
        Objects.requireNonNull(module).accept(this);
        return this;
    }
}
