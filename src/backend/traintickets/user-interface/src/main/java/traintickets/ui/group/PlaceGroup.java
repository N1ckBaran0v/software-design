package traintickets.ui.group;

import traintickets.ui.controller.PlaceController;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.get;

public final class PlaceGroup extends AbstractEndpointGroup {
    private final PlaceController placeController;

    public PlaceGroup(PlaceController placeController) {
        super("/places");
        this.placeController = Objects.requireNonNull(placeController);
    }

    @Override
    public void addEndpoints() {
        get(placeController::getPlaces);
    }
}
