package traintickets.ui.group;

import traintickets.ui.controller.FilterController;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class FilterGroup extends AbstractEndpointGroup {
    private final FilterController filterController;

    public FilterGroup(FilterController filterController) {
        super("/api/filters");
        this.filterController = Objects.requireNonNull(filterController);
    }

    @Override
    public void addEndpoints() {
        post(filterController::addFilter);
        get(filterController::getFilters);
        delete(filterController::deleteFilter);
    }
}
