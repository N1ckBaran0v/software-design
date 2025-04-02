package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.RaceId;
import traintickets.businesslogic.model.Ticket;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.payment.PaymentData;

import java.util.List;

public interface TicketRepository {
    void addTickets(List<Ticket> tickets, PaymentData paymentData);
    Iterable<Ticket> getTicketsByUser(UserId userId);
    Iterable<Ticket> getTicketsByRace(RaceId raceId);
}
