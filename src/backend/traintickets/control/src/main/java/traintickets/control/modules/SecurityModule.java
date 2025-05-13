package traintickets.control.modules;

import traintickets.businesslogic.jwt.JwtManager;
import traintickets.control.configuration.RedisConfig;
import traintickets.control.configuration.SecurityConfig;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.security.jwt.JedisConfig;
import traintickets.security.jwt.JwtConfig;
import traintickets.security.jwt.JwtManagerImpl;

public final class SecurityModule implements ContextModule {
    private final SecurityConfig securityConfig;
    private final RedisConfig redisConfig;

    public SecurityModule(SecurityConfig securityConfig, RedisConfig redisConfig) {
        this.securityConfig = securityConfig;
        this.redisConfig = redisConfig;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.addSingleton(JwtManager.class, beanProvider -> {
            var jwtConfig = new JwtConfig(securityConfig.getSecret(), securityConfig.getExpiration());
            var jedisConfig = new JedisConfig(redisConfig.getHost(), redisConfig.getPort(), redisConfig.getPassword());
            return new JwtManagerImpl(jwtConfig, jedisConfig);
        });
    }
}
