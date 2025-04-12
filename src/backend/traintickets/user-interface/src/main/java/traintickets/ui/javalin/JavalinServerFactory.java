package traintickets.ui.javalin;

import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import traintickets.ui.api.RestServer;
import traintickets.ui.api.RestServerFactory;
import traintickets.ui.api.ServerParams;
import traintickets.ui.group.AbstractEndpointGroup;
import traintickets.ui.security.ExceptionHandler;
import traintickets.ui.security.SecurityConfiguration;

import java.util.List;
import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.path;

public final class JavalinServerFactory implements RestServerFactory {
    private final List<AbstractEndpointGroup> endpointGroups;
    private final JsonMapper jsonMapper;
    private final SecurityConfiguration securityConfiguration;

    public JavalinServerFactory(List<AbstractEndpointGroup> endpointGroups,
                                JsonMapper jsonMapper,
                                SecurityConfiguration securityConfiguration) {
        this.endpointGroups = Objects.requireNonNull(endpointGroups);
        this.jsonMapper = Objects.requireNonNull(jsonMapper);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public RestServer createRestServer(ServerParams serverParams) {
        var javalin = Javalin.create(javalinConfig -> {
            javalinConfig.jetty.defaultHost = serverParams.host();
            javalinConfig.jetty.defaultPort = serverParams.port();
            javalinConfig.router.apiBuilder(() -> {
                for (var group : endpointGroups) {
                    path(group.getPath(), group);
                }
            });
            javalinConfig.useVirtualThreads = true;
            javalinConfig.jsonMapper(jsonMapper);
        });
        javalin.before(securityConfiguration::checkSessionId);
        javalin.exception(RuntimeException.class, ExceptionHandler::handle);
        return new JavalinServer(javalin);
    }
}
