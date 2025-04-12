package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.TrainService;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.ui.exception.QueryParameterNotFoundException;

import java.sql.Timestamp;
import java.util.Objects;

public final class TrainController {
    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = Objects.requireNonNull(trainService);
    }

    public void addTrain(Context ctx) {
        trainService.addTrain(ctx.cookie("sessionId"), ctx.bodyAsClass(Train.class));
    }

    public void getTrain(Context ctx) {
        ctx.json(trainService.getTrain(ctx.cookie("sessionId"), new TrainId(ctx.pathParam("trainId"))));
    }

    public void getTrains(Context ctx) {
        var start = ctx.queryParam("start");
        if (start == null) {
            throw new QueryParameterNotFoundException("start");
        }
        var end = ctx.queryParam("end");
        if (end == null) {
            throw new QueryParameterNotFoundException("end");
        }
        ctx.json(trainService.getTrains(ctx.cookie("sessionId"), Timestamp.valueOf(start), Timestamp.valueOf(end)));
    }
}
