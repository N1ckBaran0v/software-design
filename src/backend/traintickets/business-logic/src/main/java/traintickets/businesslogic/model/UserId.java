package traintickets.businesslogic.model;

import java.io.Serializable;
import java.util.Objects;

public record UserId(String id) implements Serializable {
    public UserId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
