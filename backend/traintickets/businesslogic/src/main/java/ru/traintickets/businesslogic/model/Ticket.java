package ru.traintickets.businesslogic.model;

import java.math.BigDecimal;

public record Ticket(TicketId ticketId,
                     UserId owner,
                     String passenger,
                     RaceId race,
                     int railcar,
                     Place place,
                     Schedule start,
                     Schedule end,
                     BigDecimal cost) {
}
