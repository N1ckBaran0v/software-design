package traintickets.control.modules;

import traintickets.businesslogic.session.SessionManager;
import traintickets.control.configuration.RedisConfig;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.security.session.SessionManagerImpl;

public final class SecurityModule implements ContextModule {
    private final RedisConfig redisConfig;

    public SecurityModule(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(SessionManager.class, beanProvider -> new SessionManagerImpl(redisConfig.getHost(),
                redisConfig.getPort(), redisConfig.getUsername(), redisConfig.getPassword()));
    }
}
