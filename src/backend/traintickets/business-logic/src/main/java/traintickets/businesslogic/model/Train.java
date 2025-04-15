package traintickets.businesslogic.model;

import traintickets.businesslogic.exception.InvalidEntityException;

import java.io.Serializable;
import java.util.List;

public record Train(TrainId id, String trainClass, List<RailcarId> railcars) implements Serializable {
    public void validate() {
        if (trainClass == null || trainClass.isEmpty() || railcars == null || railcars.isEmpty()) {
            throw new InvalidEntityException("All data required");
        }
        for (var railcarId : railcars) {
            if (railcarId == null) {
                throw new InvalidEntityException("All data required");
            }
        }
    }
}
