package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.RaceService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;

import java.util.Objects;

public final class RaceController {
    private final RaceService raceService;
    private final UniLogger logger;

    public RaceController(RaceService raceService, UniLoggerFactory loggerFactory) {
        this.raceService = Objects.requireNonNull(raceService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(RaceController.class);
    }

    public void addRace(Context ctx) {
        var race = ctx.bodyAsClass(Race.class);
        logger.debug("race: %s", race);
        raceService.addRace(ctx.cookie("sessionId"), race);
        logger.debug("race added");
    }

    public void getRace(Context ctx) {
        var raceId = ctx.pathParam("raceId");
        logger.debug("raceId: %s", raceId);
        ctx.json(raceService.getRace(ctx.cookie("sessionId"), new RaceId(raceId)));
        logger.debug("race got");
    }

    public void finishRace(Context ctx) {
        var raceId = ctx.pathParam("raceId");
        logger.debug("raceId: %s", raceId);
        raceService.finishRace(ctx.cookie("sessionId"), new RaceId(raceId));
        logger.debug("race finished");
    }
}
