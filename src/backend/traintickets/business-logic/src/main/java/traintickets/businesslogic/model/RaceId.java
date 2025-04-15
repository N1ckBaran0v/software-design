package traintickets.businesslogic.model;

import java.io.Serializable;
import java.util.Objects;

public record RaceId(String id) implements Serializable {
    public RaceId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
