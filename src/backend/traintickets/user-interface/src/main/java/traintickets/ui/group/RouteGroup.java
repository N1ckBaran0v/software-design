package traintickets.ui.group;

import traintickets.ui.controller.RouteController;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.get;

public final class RouteGroup extends AbstractEndpointGroup {
    private final RouteController routeController;

    public RouteGroup(RouteController routeController) {
        super("/routes");
        this.routeController = Objects.requireNonNull(routeController);
    }

    @Override
    public void addEndpoints() {
        get(routeController::getRoutes);
    }
}
