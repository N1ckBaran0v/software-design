package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.RouteService;
import traintickets.businesslogic.model.Filter;

import java.util.Objects;

public final class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = Objects.requireNonNull(routeService);
    }

    public void getRoutes(Context ctx) {
        ctx.json(routeService.getRoutes(ctx.bodyAsClass(Filter.class)));
    }
}
