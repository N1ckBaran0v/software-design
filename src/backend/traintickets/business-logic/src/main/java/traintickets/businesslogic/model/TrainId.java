package traintickets.businesslogic.model;

import java.io.Serializable;
import java.util.Objects;

public record TrainId(String id) implements Serializable {
    public TrainId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
