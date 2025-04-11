package traintickets.businesslogic.model;

import java.util.Objects;

public record UserId(String id) {
    public UserId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
