package traintickets.ui.group;

import traintickets.ui.controller.RaceController;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class RaceGroup extends AbstractEndpointGroup {
    private final RaceController raceController;

    public RaceGroup(RaceController raceController) {
        super("/api/races");
        this.raceController = raceController;
    }

    @Override
    public void addEndpoints() {
        post(raceController::addRace);
        get("/{raceId}", raceController::getRace);
        patch("/{raceId}", raceController::finishRace);
    }
}
