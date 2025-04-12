package traintickets.ui.group;

import traintickets.ui.controller.RailcarController;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public final class RailcarGroup extends AbstractEndpointGroup {
    private final RailcarController railcarController;

    public RailcarGroup(RailcarController railcarController) {
        super("/api/railcars");
        this.railcarController = Objects.requireNonNull(railcarController);
    }

    @Override
    public void addEndpoints() {
        post(railcarController::addRailcar);
        get(railcarController::getRailcars);
    }
}
