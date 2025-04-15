package traintickets.businesslogic.model;

import java.io.Serializable;
import java.util.Objects;

public record ScheduleId(String id) implements Serializable {
    public ScheduleId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
