package traintickets.businesslogic.model;

import java.util.List;

public record Train(TrainId id, String trainClass, List<RailcarId> railcars) {
}
