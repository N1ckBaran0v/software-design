package traintickets.businesslogic.model;

import traintickets.businesslogic.exception.InvalidEntityException;

import java.util.Date;
import java.util.Map;

public record Filter(UserId user,
                     String name,
                     String departure,
                     String destination,
                     int transfers,
                     Map<String, Integer> passengers,
                     Date start,
                     Date end) {
    public void validate() {
        if (user == null || name == null || departure == null || destination == null ||
                transfers < 0 || passengers == null || start== null || end == null) {
            throw new InvalidEntityException("Invalid parameters");
        }
        if (departure.equals(destination)) {
            throw new InvalidEntityException("Departure and destination are the same");
        }
        if (passengers.isEmpty()) {
            throw new InvalidEntityException("Passengers cannot be empty");
        }
        for (var entry : passengers.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null || entry.getValue() < 1) {
                throw new InvalidEntityException("Invalid passengers data");
            }
        }
        if (end.before(start)) {
            throw new InvalidEntityException("Start date cannot be after end date");
        }
    }
}
