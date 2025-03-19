package ru.traintickets.di;

import java.util.*;

final class BeanCollector {
    private final Map<Class<?>, Set<Class<?>>> singletonSubclasses = new HashMap<>();
    private final Map<Class<?>, Set<Class<?>>> prototypeSubclasses = new HashMap<>();
    private final Map<Class<?>, Set<BeanCreator<?>>> singletonCreators = new HashMap<>();
    private final Map<Class<?>, Set<BeanCreator<?>>> prototypeCreators = new HashMap<>();
    private final Map<Class<?>, Set<Object>> instances = new HashMap<>();

    <T> void addSingleton(Class<T> clazz, Class<? extends T> subclass) {
        if (!singletonSubclasses.containsKey(clazz)) {
            singletonSubclasses.put(clazz, new HashSet<>());
        }
        singletonSubclasses.get(clazz).add(subclass);
    }

    <T> void addPrototype(Class<T> clazz, Class<? extends T> subclass) {
        if (!prototypeSubclasses.containsKey(clazz)) {
            prototypeSubclasses.put(clazz, new HashSet<>());
        }
        prototypeSubclasses.get(clazz).add(subclass);
    }

    <T> void addSingleton(Class<T> clazz, BeanCreator<T> creator) {
        if (!singletonCreators.containsKey(clazz)) {
            singletonCreators.put(clazz, new HashSet<>());
        }
        singletonCreators.get(clazz).add(creator);
    }

    <T> void addPrototype(Class<T> clazz, BeanCreator<T> creator) {
        if (!singletonCreators.containsKey(clazz)) {
            singletonCreators.put(clazz, new HashSet<>());
        }
        prototypeCreators.get(clazz).add(creator);
    }

    <T> void addInstance(Class<T> clazz, T instance) {
        if (!instances.containsKey(clazz)) {
            instances.put(clazz, new HashSet<>());
        }
        instances.get(clazz).add(instance);
    }

    Map<Class<?>, Set<Class<?>>> getSingletonSubclasses() {
        return singletonSubclasses;
    }

    Map<Class<?>, Set<Class<?>>> getPrototypeSubclasses() {
        return prototypeSubclasses;
    }

    Map<Class<?>, Set<BeanCreator<?>>> getSingletonCreators() {
        return singletonCreators;
    }

    Map<Class<?>, Set<BeanCreator<?>>> getPrototypeCreators() {
        return prototypeCreators;
    }

    Map<Class<?>, Set<Object>> getInstances() {
        return instances;
    }
}
