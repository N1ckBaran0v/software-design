package traintickets.control.modules;

import traintickets.businesslogic.api.RaceService;
import traintickets.businesslogic.api.UserService;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.jwt.JwtManager;
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
import traintickets.ui.security.RuntimeExceptionHandler;
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
                    var runtimeExceptionHandler = beanProvider.getInstance(RuntimeExceptionHandler.class);
                    var loggerFactory = beanProvider.getInstance(UniLoggerFactory.class);
                    return new JavalinServerFactory(endpointGroups, runtimeExceptionHandler, loggerFactory);
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
                    var loggerFactory = beanProvider.getInstance(UniLoggerFactory.class);
                    var adminRole = securityConfig.getAdminRole().getAppName();
                    return new UserController(userService, raceService, loggerFactory, adminRole);
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
                .addSingleton(RuntimeExceptionHandler.class, RuntimeExceptionHandler.class)
                .addSingleton(SecurityConfiguration.class, beanProvider -> {
                    var jwtManager = beanProvider.getInstance(JwtManager.class);
                    var loggerFactory = beanProvider.getInstance(UniLoggerFactory.class);
                    var userRole = securityConfig.getUserRole().getAppName();
                    var carrierRole = securityConfig.getCarrierRole().getAppName();
                    var adminRole = securityConfig.getAdminRole().getAppName();
                    return new SecurityConfiguration(jwtManager, loggerFactory, userRole, carrierRole, adminRole);
                });
    }
}
