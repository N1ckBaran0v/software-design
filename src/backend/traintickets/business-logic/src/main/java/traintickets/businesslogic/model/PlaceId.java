package traintickets.businesslogic.model;

import java.io.Serializable;
import java.util.Objects;

public record PlaceId(String id) implements Serializable {
    public PlaceId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
