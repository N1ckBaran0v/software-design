package traintickets.control.modules;

import traintickets.businesslogic.api.*;
import traintickets.businesslogic.repository.*;
import traintickets.businesslogic.service.*;
import traintickets.businesslogic.session.SessionManager;
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
                    var repo = beanProvider.getInstance(UserRepository.class);
                    var manager = beanProvider.getInstance(SessionManager.class);
                    var defaultRole = securityConfig.getRoles().get("userRole");
                    var systemRole = securityConfig.getRoles().get("systemRole");
                    return new AuthServiceImpl(repo, manager, defaultRole, systemRole);
                })
                .addSingleton(CommentService.class, CommentServiceImpl.class)
                .addSingleton(FilterService.class, FilterServiceImpl.class)
                .addSingleton(RaceService.class, RaceServiceImpl.class)
                .addSingleton(RailcarService.class, RailcarServiceImpl.class)
                .addSingleton(RouteService.class, beanProvider -> {
                    var railcarRepository = beanProvider.getInstance(RailcarRepository.class);
                    var trainRepository = beanProvider.getInstance(TrainRepository.class);
                    var raceRepository = beanProvider.getInstance(RaceRepository.class);
                    var ticketRepository = beanProvider.getInstance(TicketRepository.class);
                    var systemRole = securityConfig.getRoles().get("systemRole");
                    return new RouteServiceImpl(railcarRepository, trainRepository,
                            raceRepository, ticketRepository, systemRole);
                })
                .addSingleton(TicketService.class, TicketServiceImpl.class)
                .addSingleton(TrainService.class, TrainServiceImpl.class)
                .addSingleton(UserService.class, beanProvider -> {
                    var userRepository = beanProvider.getInstance(UserRepository.class);
                    var sessionManager = beanProvider.getInstance(SessionManager.class);
                    var systemRole = securityConfig.getRoles().get("systemRole");
                    return new UserServiceImpl(userRepository, sessionManager, systemRole);
                });
    }
}
