package traintickets.ui.group;

import traintickets.ui.controller.TrainController;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class TrainGroup extends AbstractEndpointGroup {
    private final TrainController trainController;
    private final SecurityConfiguration securityConfiguration;

    public TrainGroup(TrainController trainController, SecurityConfiguration securityConfiguration) {
        super("/trains");
        this.trainController = Objects.requireNonNull(trainController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        post(ctx -> trainController.addTrain(ctx, securityConfiguration.forCarrier(ctx)));
        get("/{trainId}", ctx -> trainController.getTrain(ctx, securityConfiguration.forCarrier(ctx)));
        get(ctx -> trainController.getTrains(ctx, securityConfiguration.forCarrier(ctx)));
    }
}
