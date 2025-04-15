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
    public void saveValidate() {
        if (user == null || name == null) {
            throw new InvalidEntityException("User and name are required");
        }
        validate();
    }

    public void searchValidate() {
        if (start == null || end == null) {
            throw new InvalidEntityException("Start and end are required");
        }
        if (end.before(start)) {
            throw new InvalidEntityException("Start date cannot be after end date");
        }
        validate();
    }

    private void validate() {
        if (departure == null || destination == null || transfers < 0 || passengers == null) {
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
    }
}
