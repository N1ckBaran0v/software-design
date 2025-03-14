package ru.traintickets.businesslogic.model;

import java.math.BigDecimal;

public record Ticket(UserId owner,
                     RaceId race,
                     int railcar,
                     Place place,
                     Schedule start,
                     Schedule end,
                     BigDecimal cost) {
}
