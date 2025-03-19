package ru.traintickets.di;

import java.util.Objects;

final class SingletonCreator<T> implements BeanCreator<T> {
    private T instance;
    private final BeanCreator<? extends T> creator;

    SingletonCreator(BeanCreator<? extends T> creator) {
        this.creator = Objects.requireNonNull(creator);
    }

    @Override
    public T create(BeanProvider beanProvider) {
        if (instance == null) {
            instance = creator.create(beanProvider);
        }
        return instance;
    }
}
