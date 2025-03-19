package traintickets.dataaccess.repository;

import traintickets.businesslogic.model.RaceId;
import traintickets.businesslogic.model.Ticket;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.TicketRepository;

import java.util.List;

public final class TicketRepositoryImpl implements TicketRepository {
    @Override
    public void addTickets(List<Ticket> tickets) {
    }

    @Override
    public Iterable<Ticket> getTicketsByUser(UserId userId) {
        return null;
    }

    @Override
    public Iterable<Ticket> getTicketsByRace(RaceId raceId) {
        return null;
    }
}
