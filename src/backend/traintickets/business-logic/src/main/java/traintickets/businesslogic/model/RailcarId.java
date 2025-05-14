package traintickets.businesslogic.model;

import java.io.Serializable;
import java.util.Objects;

public record RailcarId(String id) implements Serializable {
    public RailcarId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
