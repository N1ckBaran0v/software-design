package traintickets.businesslogic.model;

import java.util.Objects;

public record TrainId(String id) {
    public TrainId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
