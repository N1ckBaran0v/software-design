package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.RailcarService;
import traintickets.businesslogic.model.Railcar;
import traintickets.ui.exception.QueryParameterNotFoundException;

import java.util.Objects;

public final class RailcarController {
    private final RailcarService railcarService;

    public RailcarController(RailcarService railcarService) {
        this.railcarService = Objects.requireNonNull(railcarService);
    }

    public void addRailcar(Context ctx) {
        railcarService.addRailcar(ctx.cookie("sessionId"), ctx.bodyAsClass(Railcar.class));
    }

    public void getRailcars(Context ctx) {
        var type = ctx.queryParam("type");
        if (type == null) {
            throw new QueryParameterNotFoundException("type");
        }
        ctx.json(railcarService.getRailcars(ctx.cookie("sessionId"), type));
    }
}
