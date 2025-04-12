package traintickets.businesslogic.model;

import java.util.Objects;

public record TicketId(String id) {
    public TicketId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
