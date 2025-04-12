package traintickets.control.modules;

import traintickets.businesslogic.api.RaceService;
import traintickets.businesslogic.api.UserService;
import traintickets.businesslogic.session.SessionManager;
import traintickets.control.configuration.SecurityConfig;
import traintickets.control.configuration.ServerConfig;
import traintickets.di.ApplicationContextBuilder;
import traintickets.di.ContextModule;
import traintickets.ui.api.Server;
import traintickets.ui.api.ServerFactory;
import traintickets.ui.api.ServerParams;
import traintickets.ui.controller.*;
import traintickets.ui.group.*;
import traintickets.ui.javalin.JavalinServerFactory;
import traintickets.ui.security.SecurityConfiguration;

public final class UserInterfaceModule implements ContextModule {
    private final ServerConfig serverConfig;
    private final SecurityConfig securityConfig;

    public UserInterfaceModule(ServerConfig serverConfig, SecurityConfig securityConfig) {
        this.serverConfig = serverConfig;
        this.securityConfig = securityConfig;
    }

    @Override
    public void accept(ApplicationContextBuilder builder) {
        builder.
                addSingleton(Server.class, beanProvider -> {
                    var restServerFactory = beanProvider.getInstance(ServerFactory.class);
                    var serverParams = new ServerParams(serverConfig.getHost(), serverConfig.getPort());
                    return restServerFactory.createRestServer(serverParams);
                })
                .addSingleton(ServerFactory.class, beanProvider -> {
                    var endpointGroups = beanProvider.getInstances(AbstractEndpointGroup.class);
                    var securityConfiguration = beanProvider.getInstance(SecurityConfiguration.class);
                    return new JavalinServerFactory(endpointGroups, securityConfiguration);
                })
                .addSingleton(AuthController.class, AuthController.class)
                .addSingleton(CommentController.class, CommentController.class)
                .addSingleton(FilterController.class, FilterController.class)
                .addSingleton(PlaceController.class, PlaceController.class)
                .addSingleton(RaceController.class, RaceController.class)
                .addSingleton(RailcarController.class, RailcarController.class)
                .addSingleton(RouteController.class, RouteController.class)
                .addSingleton(TicketController.class, TicketController.class)
                .addSingleton(TrainController.class, TrainController.class)
                .addSingleton(UserController.class, beanProvider -> {
                    var userService = beanProvider.getInstance(UserService.class);
                    var raceService = beanProvider.getInstance(RaceService.class);
                    var sessionManager = beanProvider.getInstance(SessionManager.class);
                    var adminRole = securityConfig.getRoles().get("adminRole");
                    return new UserController(userService, raceService, sessionManager, adminRole);
                })
                .addSingleton(AbstractEndpointGroup.class, AuthGroup.class)
                .addSingleton(AbstractEndpointGroup.class, CommentGroup.class)
                .addSingleton(AbstractEndpointGroup.class, FilterGroup.class)
                .addSingleton(AbstractEndpointGroup.class, PlaceGroup.class)
                .addSingleton(AbstractEndpointGroup.class, RaceGroup.class)
                .addSingleton(AbstractEndpointGroup.class, RailcarGroup.class)
                .addSingleton(AbstractEndpointGroup.class, RouteGroup.class)
                .addSingleton(AbstractEndpointGroup.class, TicketGroup.class)
                .addSingleton(AbstractEndpointGroup.class, TrainGroup.class)
                .addSingleton(AbstractEndpointGroup.class, UserGroup.class)
                .addSingleton(SecurityConfiguration.class, beanProvider -> {
                    var userRole = securityConfig.getRoles().get("userRole");
                    var carrierRole = securityConfig.getRoles().get("carrierRole");
                    var adminRole = securityConfig.getRoles().get("adminRole");
                    var sessionManager = beanProvider.getInstance(SessionManager.class);
                    return new SecurityConfiguration(sessionManager, userRole, carrierRole, adminRole);
                });
    }
}
