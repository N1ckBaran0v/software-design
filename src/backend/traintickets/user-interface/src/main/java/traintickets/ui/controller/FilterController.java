package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.FilterService;
import traintickets.businesslogic.model.Filter;

import java.util.Objects;

public final class FilterController {
    private final FilterService filterService;

    public FilterController(FilterService filterService) {
        this.filterService = Objects.requireNonNull(filterService);
    }

    public void addFilter(Context ctx) {
        filterService.addFilter(ctx.cookie("sessionId"), ctx.bodyAsClass(Filter.class));
    }

    public void getFilters(Context ctx) {
        var filterName = ctx.queryParam("filterName");
        if (filterName == null) {
            ctx.json(filterService.getFilters(ctx.cookie("sessionId")));
        } else {
            ctx.json(filterService.getFilter(ctx.cookie("sessionId"), filterName));
        }
    }

    public void deleteFilter(Context ctx) {
        filterService.deleteFilter(ctx.cookie("sessionId"), ctx.queryParam("filterId"));
    }
}
