package traintickets.control.modules;

import traintickets.businesslogic.api.*;
import traintickets.businesslogic.repository.*;
import traintickets.businesslogic.service.*;
import traintickets.businesslogic.jwt.JwtManager;
import traintickets.control.configuration.SecurityConfig;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;

public final class BusinessLogicModule implements ContextModule {
    private final SecurityConfig securityConfig;

    public BusinessLogicModule(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder
                .addSingleton(AuthService.class, beanProvider -> {
                    var userRepository = beanProvider.getInstance(UserRepository.class);
                    var jwtManager = beanProvider.getInstance(JwtManager.class);
                    var defaultRole = securityConfig.getUserRole();
                    return new AuthServiceImpl(userRepository, jwtManager, defaultRole);
                })
                .addSingleton(CommentService.class, CommentServiceImpl.class)
                .addSingleton(FilterService.class, FilterServiceImpl.class)
                .addSingleton(RaceService.class, RaceServiceImpl.class)
                .addSingleton(RailcarService.class, RailcarServiceImpl.class)
                .addSingleton(RouteService.class, RouteServiceImpl.class)
                .addSingleton(TicketService.class, TicketServiceImpl.class)
                .addSingleton(TrainService.class, TrainServiceImpl.class)
                .addSingleton(UserService.class, UserServiceImpl.class);
    }
}
