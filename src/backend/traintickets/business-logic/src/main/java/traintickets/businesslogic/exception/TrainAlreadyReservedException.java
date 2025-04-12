package traintickets.businesslogic.exception;

import traintickets.businesslogic.model.TrainId;

import java.util.Objects;

public final class TrainAlreadyReservedException extends RuntimeException {
    public TrainAlreadyReservedException(TrainId trainId) {
        super(String.format("Train %s already reserved", Objects.requireNonNull(trainId).id()));
    }
}
