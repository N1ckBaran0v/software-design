package traintickets.businesslogic.model;

import java.io.Serializable;
import java.util.Objects;

public record TicketId(String id) implements Serializable {
    public TicketId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
