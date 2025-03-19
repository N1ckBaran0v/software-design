package traintickets.businesslogic.model;

import java.util.Date;

public record Schedule(String name, Date arrival, Date departure, double multiplier) {
}
