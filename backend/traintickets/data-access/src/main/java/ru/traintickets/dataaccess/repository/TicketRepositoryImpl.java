package ru.traintickets.dataaccess.repository;

import ru.traintickets.businesslogic.model.RaceId;
import ru.traintickets.businesslogic.model.Ticket;
import ru.traintickets.businesslogic.model.UserId;
import ru.traintickets.businesslogic.repository.TicketRepository;

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
