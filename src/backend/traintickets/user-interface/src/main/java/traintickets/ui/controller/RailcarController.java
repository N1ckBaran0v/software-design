package traintickets.ui.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import traintickets.businesslogic.api.RailcarService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.Railcar;
import traintickets.ui.exception.QueryParameterNotFoundException;

import java.util.Objects;

public final class RailcarController {
    private final RailcarService railcarService;
    private final UniLogger logger;

    public RailcarController(RailcarService railcarService, UniLoggerFactory loggerFactory) {
        this.railcarService = Objects.requireNonNull(railcarService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(RailcarController.class);
    }

    public void addRailcar(Context ctx) {
        var railcar = ctx.bodyAsClass(Railcar.class);
        logger.debug("railcar: %s", railcar);
        railcarService.addRailcar(railcar);
        ctx.status(HttpStatus.CREATED);
        logger.debug("railcar added");
    }

    public void getRailcars(Context ctx) {
        var type = ctx.queryParam("type");
        logger.debug("type: %s", type);
        if (type == null) {
            throw new QueryParameterNotFoundException("type");
        }
        ctx.json(railcarService.getRailcars(type));
        logger.debug("railcars got");
    }
}
