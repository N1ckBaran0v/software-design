package traintickets.businesslogic.model;

import java.util.Objects;

public record RailcarId(String id) {
    public RailcarId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
