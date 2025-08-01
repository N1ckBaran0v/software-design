package traintickets.di;

@FunctionalInterface
public interface BeanCreator<T> {
    T create(BeanProvider beanProvider);
}
