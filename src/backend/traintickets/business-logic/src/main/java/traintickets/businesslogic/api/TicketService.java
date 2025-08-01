package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Ticket;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.payment.PaymentData;

import java.util.List;

public interface TicketService {
    void buyTickets(List<Ticket> tickets, PaymentData paymentData);
    List<Ticket> getTickets(UserId userId);
}
