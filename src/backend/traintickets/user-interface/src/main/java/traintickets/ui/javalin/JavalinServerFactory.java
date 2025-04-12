package traintickets.ui.javalin;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import traintickets.ui.api.Server;
import traintickets.ui.api.ServerFactory;
import traintickets.ui.api.ServerParams;
import traintickets.ui.group.AbstractEndpointGroup;
import traintickets.ui.security.ExceptionHandler;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.path;

public final class JavalinServerFactory implements ServerFactory {
    private final Iterable<AbstractEndpointGroup> endpointGroups;
    private final SecurityConfiguration securityConfiguration;

    public JavalinServerFactory(Iterable<AbstractEndpointGroup> endpointGroups,
                                SecurityConfiguration securityConfiguration) {
        this.endpointGroups = Objects.requireNonNull(endpointGroups);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public Server createRestServer(ServerParams serverParams) {
        var javalin = Javalin.create(javalinConfig -> {
            javalinConfig.jetty.defaultHost = serverParams.host();
            javalinConfig.jetty.defaultPort = serverParams.port();
            javalinConfig.router.apiBuilder(() -> {
                for (var group : endpointGroups) {
                    path(group.getPath(), group);
                }
            });
            javalinConfig.useVirtualThreads = true;
            javalinConfig.jsonMapper(new JavalinGson(new Gson(), true));
        });
        javalin.before(securityConfiguration::checkSessionId);
        javalin.exception(RuntimeException.class, ExceptionHandler::handle);
        return new JavalinServer(javalin);
    }
}
