package traintickets.control.modules;

import traintickets.businesslogic.jwt.JwtManager;
import traintickets.control.configuration.SecurityConfig;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.security.jwt.JwtConfig;
import traintickets.security.jwt.JwtManagerImpl;

public final class SecurityModule implements ContextModule {
    private final SecurityConfig securityConfig;

    public SecurityModule(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(JwtManager.class, beanProvider ->
                new JwtManagerImpl(new JwtConfig(securityConfig.getSecret(), securityConfig.getExpiration())));
    }
}
