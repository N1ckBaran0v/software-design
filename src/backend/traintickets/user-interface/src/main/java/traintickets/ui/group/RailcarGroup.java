package traintickets.ui.group;

import traintickets.ui.controller.RailcarController;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class RailcarGroup extends AbstractEndpointGroup {
    private final RailcarController railcarController;
    private final SecurityConfiguration securityConfiguration;

    public RailcarGroup(RailcarController railcarController, SecurityConfiguration securityConfiguration) {
        super("/railcars");
        this.railcarController = Objects.requireNonNull(railcarController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        post(ctx -> railcarController.addRailcar(ctx, securityConfiguration.forCarrier(ctx)));
        get(ctx -> railcarController.getRailcars(ctx, securityConfiguration.forCarrier(ctx)));
    }
}
