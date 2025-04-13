package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.FilterService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.Filter;

import java.util.Objects;

public final class FilterController {
    private final FilterService filterService;
    private final UniLogger logger;

    public FilterController(FilterService filterService, UniLoggerFactory loggerFactory) {
        this.filterService = Objects.requireNonNull(filterService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(FilterController.class);
    }

    public void addFilter(Context ctx) {
        filterService.addFilter(ctx.cookie("sessionId"), ctx.bodyAsClass(Filter.class));
    }

    public void getFilters(Context ctx) {
        var filterName = ctx.queryParam("filterName");
        logger.debug("filterName: %s", filterName);
        if (filterName == null) {
            ctx.json(filterService.getFilters(ctx.cookie("sessionId")));
            logger.debug("filters got");
        } else {
            ctx.json(filterService.getFilter(ctx.cookie("sessionId"), filterName));
            logger.debug("filter got");
        }
    }

    public void deleteFilter(Context ctx) {
        var filterId = ctx.queryParam("filterId");
        logger.debug("filterId: %s", filterId);
        filterService.deleteFilter(ctx.cookie("sessionId"), filterId);
        logger.debug("filter deleted");
    }
}
