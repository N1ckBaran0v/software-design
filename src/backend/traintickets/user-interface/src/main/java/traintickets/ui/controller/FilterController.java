package traintickets.ui.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import traintickets.businesslogic.api.FilterService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.transport.UserInfo;

import java.util.List;
import java.util.Objects;

public final class FilterController {
    private final FilterService filterService;
    private final UniLogger logger;

    public FilterController(FilterService filterService, UniLoggerFactory loggerFactory) {
        this.filterService = Objects.requireNonNull(filterService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(FilterController.class);
    }

    public void addFilter(Context ctx, UserInfo userInfo) {
        var filter = ctx.bodyAsClass(Filter.class);
        logger.debug("filter: %s", filter);
        filterService.addFilter(userInfo, filter);
        ctx.status(HttpStatus.CREATED);
        logger.debug("filter added");
    }

    public void getFilters(Context ctx, UserInfo userInfo) {
        var filterName = ctx.queryParam("filterName");
        logger.debug("filterName: %s", filterName);
        if (filterName == null) {
            ctx.json(filterService.getFilters(userInfo));
            logger.debug("filters got");
        } else {
            ctx.json(List.of(filterService.getFilter(userInfo, filterName)));
            logger.debug("filter got");
        }
    }

    public void deleteFilter(Context ctx, UserInfo userInfo) {
        var filterId = ctx.queryParam("filterId");
        logger.debug("filterId: %s", filterId);
        filterService.deleteFilter(userInfo, filterId);
        ctx.status(HttpStatus.NO_CONTENT);
        logger.debug("filter deleted");
    }
}
