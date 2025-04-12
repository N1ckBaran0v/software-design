package traintickets.ui.group;

import traintickets.ui.controller.TrainController;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class TrainGroup extends AbstractEndpointGroup {
    private final TrainController trainController;
    private final SecurityConfiguration securityConfiguration;

    public TrainGroup(TrainController trainController, SecurityConfiguration securityConfiguration) {
        super("/api/trains");
        this.trainController = Objects.requireNonNull(trainController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        before(securityConfiguration::forCarrier);
        post(trainController::addTrain);
        get("/{trainId}", trainController::getTrain);
        get(trainController::getTrains);
    }
}
