package ru.traintickets.businesslogic.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public record Filter(UserId user,
                     String name,
                     String trainClass,
                     List<String> passengers,
                     Date start,
                     Date end,
                     BigDecimal minCost,
                     BigDecimal maxCost) {
}
