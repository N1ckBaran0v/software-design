package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.TrainService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.ui.exception.QueryParameterNotFoundException;

import java.sql.Timestamp;
import java.util.Objects;

public final class TrainController {
    private final TrainService trainService;
    private final UniLogger logger;

    public TrainController(TrainService trainService, UniLoggerFactory loggerFactory) {
        this.trainService = Objects.requireNonNull(trainService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(TrainController.class);
    }

    public void addTrain(Context ctx) {
        var train = ctx.bodyAsClass(Train.class);
        logger.debug("train: %s", train);
        trainService.addTrain(ctx.cookie("sessionId"), train);
        logger.debug("train added");
    }

    public void getTrain(Context ctx) {
        var trainId = ctx.pathParam("trainId");
        logger.debug("trainId: %s", trainId);
        ctx.json(trainService.getTrain(ctx.cookie("sessionId"), new TrainId(trainId)));
        logger.debug("train got");
    }

    public void getTrains(Context ctx) {
        var start = ctx.queryParam("start");
        logger.debug("start: %s", start);
        if (start == null) {
            throw new QueryParameterNotFoundException("start");
        }
        var end = ctx.queryParam("end");
        logger.debug("end: %s", end);
        if (end == null) {
            throw new QueryParameterNotFoundException("end");
        }
        ctx.json(trainService.getTrains(ctx.cookie("sessionId"), Timestamp.valueOf(start), Timestamp.valueOf(end)));
        logger.debug("trains got");
    }
}
