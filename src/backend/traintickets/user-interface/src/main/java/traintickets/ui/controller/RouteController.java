package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.RouteService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.Filter;
import traintickets.ui.exception.InvalidQueryParameterException;
import traintickets.ui.exception.QueryParameterNotFoundException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public final class RouteController {
    private final RouteService routeService;
    private final UniLogger logger;

    public RouteController(RouteService routeService, UniLoggerFactory loggerFactory) {
        this.routeService = Objects.requireNonNull(routeService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(RouteController.class);
    }

    public void getRoutes(Context ctx) {
        var filter = getFilter(ctx);
        logger.debug("filter: %s", filter);
        ctx.json(routeService.getRoutes(filter));
        logger.debug("routes got");
    }

    private Filter getFilter(Context ctx) {
        var name = getParamValue(ctx, "name");
        var departure = getParamValue(ctx, "departure");
        var destination = getParamValue(ctx, "destination");
        var transfersString = getParamValue(ctx, "transfers");
        var transfers = 0;
        try {
            transfers = Integer.parseInt(transfersString);
        } catch (NumberFormatException e) {
            throw new InvalidQueryParameterException("transfers");
        }
        var passengers = new HashMap<String, Integer>();
        var startTime = getParamValue(ctx, "start");
        var start = new Date();
        try {
            start = Timestamp.valueOf(startTime);
        } catch (IllegalArgumentException e) {
            throw new InvalidQueryParameterException("start");
        }
        var endTime = getParamValue(ctx, "end");
        var end = new Date();
        try {
            end = Timestamp.valueOf(endTime);
        } catch (IllegalArgumentException e) {
            throw new InvalidQueryParameterException("end");
        }
        for (var param : ctx.queryParamMap().entrySet()) {
            if (param.getKey().startsWith("passenger_")) {
                try {
                    passengers.put(param.getKey().substring(10), Integer.parseInt(param.getValue().getFirst()));
                } catch (NumberFormatException e) {
                    throw new InvalidQueryParameterException(param.getKey());
                }
            }
        }
        return new Filter(null, name, departure, destination, transfers, passengers, start, end);
    }

    private String getParamValue(Context ctx, String name) {
        var param = ctx.queryParam(name);
        if (param == null) {
            throw new QueryParameterNotFoundException(name);
        }
        return param;
    }
}
