package ru.traintickets.businesslogic.model;

import java.math.BigDecimal;

public record Place(int number, String description, String purpose, BigDecimal cost) {
}
