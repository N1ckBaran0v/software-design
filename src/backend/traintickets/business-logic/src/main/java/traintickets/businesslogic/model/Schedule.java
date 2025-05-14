package traintickets.businesslogic.model;

import java.io.Serializable;
import java.util.Date;

public record Schedule(ScheduleId id,
                       String name,
                       Date arrival,
                       Date departure,
                       double multiplier) implements Serializable {
    public void validate() {
        if (name == null || name.isBlank() || (arrival == null && departure == null) || multiplier < 0) {
            throw new IllegalArgumentException("All data required");
        }
        if (arrival != null && departure != null && arrival.after(departure)) {
            throw new IllegalArgumentException("Arrival time cannot be after arrival");
        }
    }
}
