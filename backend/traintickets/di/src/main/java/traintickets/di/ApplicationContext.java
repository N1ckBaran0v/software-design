package traintickets.di;

public final class ApplicationContext {
    private final BeanProvider beanProvider;

    ApplicationContext(BeanCollector beanCollector) {
        this.beanProvider = new BeanProvider(beanCollector);
    }

    public static ApplicationContextBuilder builder() {
        return new ApplicationContextBuilder();
    }

    public <T> T getInstance(Class<T> clazz) {
        return beanProvider.getInstance(clazz);
    }

    public <T> Iterable<T> getInstances(Class<T> clazz) {
        return beanProvider.getInstances(clazz);
    }
}
