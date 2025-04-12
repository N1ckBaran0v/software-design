package traintickets.businesslogic.model;

import java.util.Objects;

public record PlaceId(String id) {
    public PlaceId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
