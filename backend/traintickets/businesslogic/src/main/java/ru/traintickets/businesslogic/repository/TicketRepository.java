package ru.traintickets.businesslogic.repository;

import ru.traintickets.businesslogic.model.RaceId;
import ru.traintickets.businesslogic.model.Ticket;
import ru.traintickets.businesslogic.model.UserId;

import java.util.List;

public interface TicketRepository {
    void addTickets(List<Ticket> tickets);
    Iterable<Ticket> getTicketsByUser(UserId userId);
    Iterable<Ticket> getTicketsByRace(RaceId raceId);
}
