package traintickets.businesslogic.model;

import java.util.Date;

public record Schedule(ScheduleId id, String name, Date arrival, Date departure, double multiplier) {
}
