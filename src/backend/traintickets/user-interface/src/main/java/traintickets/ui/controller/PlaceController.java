package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.RouteService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.RaceId;
import traintickets.businesslogic.model.ScheduleId;
import traintickets.ui.exception.QueryParameterNotFoundException;

import java.util.Objects;

public final class PlaceController {
    private final RouteService routeService;
    private final UniLogger logger;

    public PlaceController(RouteService routeService, UniLoggerFactory loggerFactory) {
        this.routeService = Objects.requireNonNull(routeService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(PlaceController.class);
    }

    public void getPlaces(Context ctx) {
        var raceId = ctx.queryParam("raceId");
        logger.debug("raceId: %s", raceId);
        if (raceId == null) {
            throw new QueryParameterNotFoundException("raceId");
        }
        var departureId = ctx.queryParam("departureId");
        logger.debug("departureId: %s", departureId);
        if (departureId == null) {
            throw new QueryParameterNotFoundException("departureId");
        }
        var destinationId = ctx.queryParam("destinationId");
        logger.debug("destinationId: %s", destinationId);
        if (destinationId == null) {
            throw new QueryParameterNotFoundException("arrivalId");
        }
        ctx.json(routeService.getFreePlaces(new RaceId(raceId),
                new ScheduleId(departureId), new ScheduleId(destinationId)));
        logger.debug("places got");
    }
}
