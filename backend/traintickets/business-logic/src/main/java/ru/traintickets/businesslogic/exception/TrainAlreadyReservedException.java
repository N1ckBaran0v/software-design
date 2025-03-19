package ru.traintickets.businesslogic.exception;

import ru.traintickets.businesslogic.model.TrainId;

import java.util.Objects;

public final class TrainAlreadyReservedException extends Exception {
    public TrainAlreadyReservedException(TrainId trainId) {
        super(String.format("Train %d already reserved", Objects.requireNonNull(trainId).id()));
    }
}
