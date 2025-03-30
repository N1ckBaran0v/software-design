package traintickets.businesslogic.model;

import java.math.BigDecimal;

public record Place(PlaceId id, int number, String description, String purpose, BigDecimal cost) {
}
