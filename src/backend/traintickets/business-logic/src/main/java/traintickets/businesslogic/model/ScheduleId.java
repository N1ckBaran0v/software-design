package traintickets.businesslogic.model;

import java.util.Objects;

public record ScheduleId(String id) {
    public ScheduleId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
