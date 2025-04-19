package traintickets.businesslogic.service;

import traintickets.businesslogic.api.TicketService;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.model.Ticket;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.payment.PaymentData;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.businesslogic.transport.UserInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = Objects.requireNonNull(ticketRepository);
    }

    @Override
    public void buyTickets(UserInfo userInfo, List<Ticket> tickets, PaymentData paymentData) {
        if (tickets.isEmpty()) {
            throw new InvalidEntityException("Tickets cannot be empty");
        }
        var sum = BigDecimal.ZERO;
        for (var ticket : tickets) {
            ticket.validate();
            sum = sum.add(ticket.cost());
        }
        paymentData.setSum(sum);
        ticketRepository.addTickets(userInfo.role(), tickets, paymentData);
    }

    @Override
    public List<Ticket> getTickets(UserInfo userInfo, UserId userId) {
        return StreamSupport.stream(ticketRepository
                .getTicketsByUser(userInfo.role(), userId).spliterator(), false).toList();
    }
}
