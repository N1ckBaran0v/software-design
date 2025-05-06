package traintickets.ui.group;

import traintickets.ui.controller.FilterController;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class FilterGroup extends AbstractEndpointGroup {
    private final FilterController filterController;
    private final SecurityConfiguration securityConfiguration;

    public FilterGroup(FilterController filterController, SecurityConfiguration securityConfiguration) {
        super("/filters");
        this.filterController = Objects.requireNonNull(filterController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        post(ctx -> filterController.addFilter(ctx, securityConfiguration.forUser(ctx)));
        get(ctx -> filterController.getFilters(ctx, securityConfiguration.forUser(ctx)));
        delete(ctx -> filterController.deleteFilter(ctx, securityConfiguration.forUser(ctx)));
    }
}
