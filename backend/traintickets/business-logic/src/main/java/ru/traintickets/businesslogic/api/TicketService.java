package ru.traintickets.businesslogic.api;

import ru.traintickets.businesslogic.model.Ticket;
import ru.traintickets.businesslogic.model.UserId;

import java.util.List;

public interface TicketService {
    void buyTickets(List<Ticket> tickets);
    List<Ticket> getTickets(UserId userId);
}
