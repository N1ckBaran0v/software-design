package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.RouteService;
import traintickets.businesslogic.model.RaceId;
import traintickets.businesslogic.model.ScheduleId;
import traintickets.ui.exception.QueryParameterNotFoundException;

import java.util.Objects;

public final class PlaceController {
    private final RouteService routeService;

    public PlaceController(RouteService routeService) {
        this.routeService = Objects.requireNonNull(routeService);
    }

    public void getPlaces(Context ctx) {
        var raceId = ctx.queryParam("raceId");
        if (raceId == null) {
            throw new QueryParameterNotFoundException("raceId");
        }
        var departureId = ctx.queryParam("departureId");
        if (departureId == null) {
            throw new QueryParameterNotFoundException("departureId");
        }
        var destinationId = ctx.queryParam("destinationId");
        if (destinationId == null) {
            throw new QueryParameterNotFoundException("arrivalId");
        }
        ctx.json(routeService.getFreePlaces(new RaceId(raceId),
                new ScheduleId(departureId), new ScheduleId(destinationId)));
    }
}
