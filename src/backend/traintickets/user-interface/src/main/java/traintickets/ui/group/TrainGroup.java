package traintickets.ui.group;

import traintickets.ui.controller.TrainController;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public final class TrainGroup extends AbstractEndpointGroup {
    private final TrainController trainController;

    public TrainGroup(TrainController trainController) {
        super("/api/trains");
        this.trainController = Objects.requireNonNull(trainController);
    }

    @Override
    public void addEndpoints() {
        post(trainController::addTrain);
        get("/{trainId}", trainController::getTrain);
        get(trainController::getTrains);
    }
}
