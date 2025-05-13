package traintickets.ui.javalin;

import io.javalin.Javalin;
import io.javalin.config.JettyConfig;
import io.javalin.http.GatewayTimeoutResponse;
import io.javalin.http.HttpStatus;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.ui.api.Server;
import traintickets.ui.api.ServerFactory;
import traintickets.ui.api.ServerParams;
import traintickets.ui.group.AbstractEndpointGroup;
import traintickets.ui.security.RuntimeExceptionHandler;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.before;
import static io.javalin.apibuilder.ApiBuilder.path;

public final class JavalinServerFactory implements ServerFactory {
    private final Iterable<AbstractEndpointGroup> endpointGroups;
    private final RuntimeExceptionHandler runtimeExceptionHandler;
    private final UniLogger logger;

    public JavalinServerFactory(Iterable<AbstractEndpointGroup> endpointGroups,
                                RuntimeExceptionHandler runtimeExceptionHandler,
                                UniLoggerFactory loggerFactory) {
        this.endpointGroups = Objects.requireNonNull(endpointGroups);
        this.runtimeExceptionHandler = Objects.requireNonNull(runtimeExceptionHandler);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(Server.class);
    }

    @Override
    public Server createRestServer(ServerParams serverParams) {
        var javalin = Javalin.create(javalinConfig -> {
            javalinConfig.jetty.defaultHost = serverParams.host();
            javalinConfig.jetty.defaultPort = serverParams.port();
            javalinConfig.jetty.timeoutStatus = HttpStatus.GATEWAY_TIMEOUT.getCode();
            javalinConfig.jetty.modifyHttpConfiguration(config -> config.setIdleTimeout(serverParams.timeout()));
            javalinConfig.router.apiBuilder(() -> {
                before(ctx -> logger.debug("method: %s, path: %s", ctx.method(), ctx.path()));
                path("/api/v1", () -> {
                    for (var group : endpointGroups) {
                        path(group.getPath(), group);
                    }
                });
            });
            javalinConfig.useVirtualThreads = true;
            javalinConfig.jsonMapper(new GsonMapper());
        });
        javalin.exception(RuntimeException.class, runtimeExceptionHandler);
        return new JavalinServer(javalin);
    }
}
