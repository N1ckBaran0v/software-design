package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Ticket;
import traintickets.businesslogic.model.UserId;

import java.util.List;

public interface TicketService {
    void buyTickets(List<Ticket> tickets);
    List<Ticket> getTickets(UserId userId);
}
