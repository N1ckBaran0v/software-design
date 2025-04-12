package traintickets.ui.javalin;

import io.javalin.Javalin;
import traintickets.ui.api.RestServer;
import traintickets.ui.api.RestServerFactory;
import traintickets.ui.api.ServerParams;
import traintickets.ui.group.AbstractEndpointGroup;

import java.util.List;
import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.path;

public final class JavalinServerFactory implements RestServerFactory {
    private List<AbstractEndpointGroup> endpointGroups;

    public JavalinServerFactory(List<AbstractEndpointGroup> endpointGroups) {
        this.endpointGroups = Objects.requireNonNull(endpointGroups);
    }

    @Override
    public RestServer createRestServer(ServerParams serverParams) {
        var javalin = Javalin.create(javalinConfig -> {
            javalinConfig.useVirtualThreads = true;
            javalinConfig.jetty.defaultHost = serverParams.host();
            javalinConfig.jetty.defaultPort = serverParams.port();
            javalinConfig.router.apiBuilder(() -> {
                for (var group : endpointGroups) {
                    path(group.getPath(), group);
                }
            });
        });
        return new JavalinServer(javalin);
    }
}
