package traintickets.businesslogic.model;

import traintickets.businesslogic.exception.InvalidEntityException;

import java.io.Serializable;
import java.util.List;

public record Race(RaceId id, TrainId trainId, List<Schedule> schedule, boolean finished) implements Serializable {
    public void validate() {
        if (trainId == null || schedule == null || finished) {
            throw new InvalidEntityException("All data required");
        }
        if (schedule.size() < 2) {
            throw new InvalidEntityException("Schedule requires at least 2 stations");
        }
        var curr = schedule.getFirst();
        if (curr == null) {
            throw new InvalidEntityException("Schedule cannot be null");
        }
        curr.validate();
        if (curr.multiplier() != 0.0) {
            throw new InvalidEntityException("Schedule requires a multiplier 0 of 1");
        }
        for (var i = 1; i < schedule.size(); ++i) {
            var prev = curr;
            curr = schedule.get(i);
            if (curr == null) {
                throw new InvalidEntityException("Schedule cannot be null");
            }
            curr.validate();
            if (curr.arrival() == null || prev.departure() == null || curr.arrival().before(prev.departure())) {
                throw new InvalidEntityException("Schedule contains invalid arrival and departure time");
            }
            if (curr.name().equals(prev.name())) {
                throw new InvalidEntityException("Schedule contains duplicate names");
            }
            if (curr.multiplier() <= prev.multiplier()) {
                throw new InvalidEntityException("Schedule contains invalid multiplier");
            }
        }
    }
}
