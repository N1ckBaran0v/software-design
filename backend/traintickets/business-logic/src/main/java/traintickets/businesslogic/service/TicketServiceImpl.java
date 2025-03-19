package traintickets.businesslogic.service;

import traintickets.businesslogic.api.TicketService;
import traintickets.businesslogic.model.Ticket;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.TicketRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = Objects.requireNonNull(ticketRepository);
    }

    @Override
    public void buyTickets(List<Ticket> tickets) {
        ticketRepository.addTickets(tickets);
    }

    @Override
    public List<Ticket> getTickets(UserId userId) {
        return StreamSupport.stream(ticketRepository.getTicketsByUser(userId).spliterator(), false).toList();
    }
}
