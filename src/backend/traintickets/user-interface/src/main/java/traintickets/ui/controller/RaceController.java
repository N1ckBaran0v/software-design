package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.RaceService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;

import java.util.Objects;

public final class RaceController {
    private final RaceService raceService;
    private final UniLogger logger;

    public RaceController(RaceService raceService) {
        this.raceService = Objects.requireNonNull(raceService);
    }

    public void addRace(Context ctx) {
        raceService.addRace(ctx.cookie("sessionId"), ctx.bodyAsClass(Race.class));
    }

    public void getRace(Context ctx) {
        ctx.json(raceService.getRace(ctx.cookie("sessionId"), new RaceId(ctx.pathParam("raceId"))));
    }

    public void finishRace(Context ctx) {
        raceService.finishRace(ctx.cookie("sessionId"), new RaceId(ctx.pathParam("raceId")));
    }
}
