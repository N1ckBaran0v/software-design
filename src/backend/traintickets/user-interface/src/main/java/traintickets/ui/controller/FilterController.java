package traintickets.ui.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
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
        var filter = ctx.bodyAsClass(Filter.class);
        logger.debug("filter: %s", filter);
        filterService.addFilter(ctx.sessionAttributeMap().get("id").toString(), filter);
        ctx.status(HttpStatus.CREATED);
        logger.debug("filter added");
    }

    public void getFilters(Context ctx) {
        var filterName = ctx.queryParam("filterName");
        logger.debug("filterName: %s", filterName);
        if (filterName == null) {
            ctx.json(filterService.getFilters(ctx.sessionAttributeMap().get("id").toString()));
            logger.debug("filters got");
        } else {
            ctx.json(filterService.getFilter(ctx.sessionAttributeMap().get("id").toString(), filterName));
            logger.debug("filter got");
        }
    }

    public void deleteFilter(Context ctx) {
        var filterId = ctx.queryParam("filterId");
        logger.debug("filterId: %s", filterId);
        filterService.deleteFilter(ctx.sessionAttributeMap().get("id").toString(), filterId);
        ctx.status(HttpStatus.NO_CONTENT);
        logger.debug("filter deleted");
    }
}
