package traintickets.businesslogic.model;

import traintickets.businesslogic.exception.InvalidEntityException;

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
                     Date end) {
    public void validate() {
        if (user == null || name == null || departure == null || destination == null || trainClass == null ||
                transfers < 0 || passengers == null || start== null || end == null) {
            throw new InvalidEntityException("Invalid parameters");
        }
        if (departure.equals(destination)) {
            throw new InvalidEntityException("Departure and destination are the same");
        }
        if (passengers.isEmpty()) {
            throw new InvalidEntityException("Passengers cannot be empty");
        }
        if (end.before(start)) {
            throw new InvalidEntityException("Start date cannot be after end date");
        }
    }
}
