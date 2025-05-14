package traintickets.businesslogic.model;

import traintickets.businesslogic.exception.InvalidEntityException;

import java.io.Serializable;
import java.math.BigDecimal;

public record Place(PlaceId id,
                    int number,
                    String description,
                    String purpose,
                    BigDecimal cost) implements Serializable {
    public void validate() {
        if (number < 1 || description == null || purpose == null ||
                cost == null || cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidEntityException("Invalid place data");
        }
    }
}
