package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.RouteService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.Filter;

import java.util.Objects;

public final class RouteController {
    private final RouteService routeService;
    private final UniLogger logger;

    public RouteController(RouteService routeService, UniLoggerFactory loggerFactory) {
        this.routeService = Objects.requireNonNull(routeService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(RouteController.class);
    }

    public void getRoutes(Context ctx) {
        var filter = ctx.bodyAsClass(Filter.class);
        logger.debug("filter: %s", filter);
        ctx.json(routeService.getRoutes(filter));
        logger.debug("routes got");
    }
}
