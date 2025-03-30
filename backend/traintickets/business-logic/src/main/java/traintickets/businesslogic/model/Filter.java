package traintickets.businesslogic.model;

import traintickets.businesslogic.exception.InvalidEntityException;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;

public record Filter(UserId user,
                     String name,
                     String departure,
                     String destination,
                     String trainClass,
                     int transfers,
                     List<String> passengers,
                     Date start,
                     Date end,
                     BigDecimal minCost,
                     BigDecimal maxCost) {

    public void validate() {
        if (user == null || name == null || departure == null || destination == null || trainClass == null ||
                transfers < 0 || passengers == null || start== null || end == null ||
                minCost == null || maxCost == null) {
            throw new InvalidEntityException("Invalid parameters");
        }
        if (passengers.isEmpty()) {
            throw new InvalidEntityException("Passengers cannot be empty");
        }
        if (end.before(start)) {
            throw new InvalidEntityException("Start date cannot be after end date");
        }
        if (minCost.compareTo(maxCost) > 0) {
            throw new InvalidEntityException("Min cost cannot be greater than max cost");
        }
    }
}
