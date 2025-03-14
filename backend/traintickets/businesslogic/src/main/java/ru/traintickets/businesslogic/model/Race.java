package ru.traintickets.businesslogic.model;

import java.util.List;

public record Race(RaceId id, TrainId trainId, List<Schedule> schedule) {
}
