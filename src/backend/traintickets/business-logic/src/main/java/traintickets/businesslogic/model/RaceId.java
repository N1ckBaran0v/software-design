package traintickets.businesslogic.model;

import java.util.Objects;

public record RaceId(String id) {
    public RaceId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
