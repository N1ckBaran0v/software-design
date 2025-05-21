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
        post(ctx -> {
            securityConfiguration.forCarrier(ctx);
            raceController.addRace(ctx);
        });
        get("/{raceId}", raceController::getRace);
        patch("/{raceId}", ctx -> {
            securityConfiguration.forCarrier(ctx);
            raceController.finishRace(ctx);
        });
    }
}
