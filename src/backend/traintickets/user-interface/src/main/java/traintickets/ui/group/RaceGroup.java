package traintickets.ui.group;

import traintickets.ui.controller.RaceController;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class RaceGroup extends AbstractEndpointGroup {
    private final RaceController raceController;
    private final SecurityConfiguration securityConfiguration;

    public RaceGroup(RaceController raceController, SecurityConfiguration securityConfiguration) {
        super("/races");
        this.raceController = Objects.requireNonNull(raceController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        post(ctx -> raceController.addRace(ctx, securityConfiguration.forCarrier(ctx)));
        get("/{raceId}", raceController::getRace);
        patch("/{raceId}", ctx -> raceController.finishRace(ctx, securityConfiguration.forCarrier(ctx)));
    }
}
