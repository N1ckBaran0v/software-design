package ru.traintickets.businesslogic.model;

import java.sql.Time;

public record Schedule(String name, Time arrival, Time departure, double multiplier) {
}
