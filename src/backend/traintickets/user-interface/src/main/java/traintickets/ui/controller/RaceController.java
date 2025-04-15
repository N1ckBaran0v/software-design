package traintickets.ui.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import traintickets.businesslogic.api.RaceService;
import traintickets.businesslogic.api.RouteService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;

import java.util.Objects;

public final class RaceController {
    private final RaceService raceService;
    private final RouteService routeService;
    private final UniLogger logger;

    public RaceController(RaceService raceService, RouteService routeService, UniLoggerFactory loggerFactory) {
        this.raceService = Objects.requireNonNull(raceService);
        this.routeService = Objects.requireNonNull(routeService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(RaceController.class);
    }

    public void addRace(Context ctx) {
        var race = ctx.bodyAsClass(Race.class);
        logger.debug("race: %s", race);
        raceService.addRace(ctx.cookie("sessionId"), race);
        ctx.status(HttpStatus.CREATED);
        logger.debug("race added");
    }

    public void getRace(Context ctx) {
        var raceId = ctx.pathParam("raceId");
        logger.debug("raceId: %s", raceId);
        ctx.json(routeService.getRace(new RaceId(raceId)));
        logger.debug("race got");
    }

    public void finishRace(Context ctx) {
        var raceId = ctx.pathParam("raceId");
        logger.debug("raceId: %s", raceId);
        raceService.finishRace(ctx.cookie("sessionId"), new RaceId(raceId));
        logger.debug("race finished");
    }
}
