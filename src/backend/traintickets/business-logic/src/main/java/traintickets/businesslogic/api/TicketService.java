package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Ticket;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.payment.PaymentData;
import traintickets.businesslogic.transport.UserInfo;

import java.util.List;

public interface TicketService {
    void buyTickets(UserInfo userInfo, List<Ticket> tickets, PaymentData paymentData);
    List<Ticket> getTickets(UserInfo userInfo, UserId userId);
}
