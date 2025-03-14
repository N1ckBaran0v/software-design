package ru.traintickets.businesslogic.model;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.List;

public record Filter(UserId user,
                     String trainClass,
                     List<String> passengers,
                     Time start,
                     Time end,
                     BigDecimal minCost,
                     BigDecimal maxCost) {
}
