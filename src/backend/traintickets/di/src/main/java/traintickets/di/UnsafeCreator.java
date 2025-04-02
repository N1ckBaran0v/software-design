package traintickets.di;

@FunctionalInterface
interface UnsafeCreator<T> {
    T create(BeanProvider beanProvider) throws Exception;
}
