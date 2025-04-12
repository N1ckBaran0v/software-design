package traintickets.ui.group;

import io.javalin.http.HandlerType;
import traintickets.ui.controller.RaceController;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class RaceGroup extends AbstractEndpointGroup {
    private final RaceController raceController;
    private final SecurityConfiguration securityConfiguration;

    public RaceGroup(RaceController raceController, SecurityConfiguration securityConfiguration) {
        super("/api/races");
        this.raceController = Objects.requireNonNull(raceController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        before(ctx -> {
            if (!ctx.method().equals(HandlerType.GET)) {
                securityConfiguration.forCarrier(ctx);
            }
        });
        post(raceController::addRace);
        get("/{raceId}", raceController::getRace);
        patch("/{raceId}", raceController::finishRace);
    }
}
