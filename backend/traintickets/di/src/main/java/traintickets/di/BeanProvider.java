package traintickets.di;

import java.lang.reflect.Constructor;
import java.util.*;

public final class BeanProvider {
    private final Map<Class<?>,  Set<BeanCreator<?>>> beans = new HashMap<>();
    private final List<Class<?>> classes = new LinkedList<>();
    private final Set<Class<?>> used = new HashSet<>();

    BeanProvider(BeanCollector collector) {
        var instances = collector.getInstances();
        for (var clazz : instances.keySet()) {
            beans.put(clazz, new HashSet<>());
            for (var instance : instances.get(clazz)) {
                beans.get(clazz).add(provider -> instance);
            }
        }
        var singletonCreators = collector.getSingletonCreators();
        for (var clazz : singletonCreators.keySet()) {
            if (!beans.containsKey(clazz)) {
                beans.put(clazz, new HashSet<>());
            }
            for (var creator : singletonCreators.get(clazz)) {
                beans.get(clazz).add(getSingletonCreator(clazz, creator));
            }
        }
        var singletonSubclasses = collector.getSingletonSubclasses();
        for (var clazz : singletonSubclasses.keySet()) {
            if (!beans.containsKey(clazz)) {
                beans.put(clazz, new HashSet<>());
            }
            for (var subclass : singletonSubclasses.get(clazz)) {
                beans.get(clazz).add(getSingletonCreator(clazz, getSafeCreator(subclass)));
            }
        }
        var prototypeCreators = collector.getPrototypeCreators();
        for (var clazz : prototypeCreators.keySet()) {
            if (!beans.containsKey(clazz)) {
                beans.put(clazz, new HashSet<>());
            }
            for (var creator : prototypeCreators.get(clazz)) {
                beans.get(clazz).add(creator);
            }
        }
        var prototypeSubclasses = collector.getPrototypeSubclasses();
        for (var clazz : prototypeSubclasses.keySet()) {
            if (!beans.containsKey(clazz)) {
                beans.put(clazz, new HashSet<>());
            }
            for (var subclass : prototypeSubclasses.get(clazz)) {
                beans.get(clazz).add(getSafeCreator(subclass));
            }
        }
        for (var beanCreators : beans.values()) {
            for (var creator : beanCreators) {
                creator.create(this);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> clazz) {
        Objects.requireNonNull(clazz);
        var subclasses = beans.get(clazz);
        if (subclasses == null) {
            throw new NoSuchBeanException(clazz);
        }
        if (subclasses.size() > 1) {
            throw new MultipleSubclassesDetectedException(clazz);
        }
        return (T) subclasses.iterator().next().create(this);
    }

    @SuppressWarnings("unchecked")
    public <T> Iterable<T> getInstances(Class<T> clazz) {
        Objects.requireNonNull(clazz);
        var subclasses = beans.get(clazz);
        if (subclasses == null) {
            throw new NoSuchBeanException(clazz);
        }
        return subclasses.stream().map(creator -> (T) creator.create(this)).toList();
    }

    @SuppressWarnings("unchecked")
    private <T> BeanCreator<T> getSingletonCreator(Class<T> ignoredClazz, BeanCreator<?> creator) {
        return new SingletonCreator<>((BeanCreator<? extends T>) creator);
    }

    @SuppressWarnings("unchecked")
    private <T> UnsafeCreator<T> getCreator(Class<T> clazz) {
        return beanProvider -> {
            var constructors = clazz.getConstructors();
            if (constructors.length != 1) {
                throw new InvalidConstructorCountException(clazz, constructors.length);
            }
            var constructor = (Constructor<T>) constructors[0];
            var types = constructor.getParameterTypes();
            var params = new Object[types.length];
            for (var i = 0; i < types.length; ++i) {
                var type = types[i];
                var bean = beans.get(type);
                if (bean == null) {
                    throw new NoSuchBeanException(type);
                } else if (bean.size() > 1) {
                    throw new MultipleSubclassesDetectedException(type);
                }
                params[i] = bean.iterator().next().create(this);
            }
            return constructor.newInstance(params);
        };
    }

    private <T> BeanCreator<T> getSafeCreator(Class<T> clazz) {
        var unsafeCreator = getCreator(clazz);
        return provider -> {
            classes.add(clazz);
            if (used.contains(clazz)) {
                var names = classes.stream().map(Class::getName).toList();
                classes.removeLast();
                throw new CycleDetectedException(names);
            }
            used.add(clazz);
            try {
                return unsafeCreator.create(provider);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                used.remove(clazz);
                classes.removeLast();
            }
        };
    }
}
