package traintickets.businesslogic.model;

import traintickets.businesslogic.exception.InvalidEntityException;

import java.math.BigDecimal;

public record Ticket(TicketId ticketId,
                     UserId owner,
                     String passenger,
                     RaceId race,
                     int railcar,
                     Place place,
                     Schedule start,
                     Schedule end,
                     BigDecimal cost) {
    public void validate() {
        if (owner == null || passenger == null || race == null || railcar < 1 ||
                place == null || start == null || end == null || cost == null) {
            throw new InvalidEntityException("All data required");
        }
        start.validate();
        end.validate();
        if (start.departure() == null || end.arrival() == null || start.departure().after(end.arrival())) {
            throw new InvalidEntityException("Invalid departure and arrival times");
        }
        if (cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidEntityException("Invalid cost");
        }
    }
}
