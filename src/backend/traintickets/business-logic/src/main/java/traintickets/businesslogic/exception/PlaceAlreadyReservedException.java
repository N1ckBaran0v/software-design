package traintickets.businesslogic.exception;

import traintickets.businesslogic.model.RaceId;

public final class PlaceAlreadyReservedException extends RuntimeException {
    public PlaceAlreadyReservedException(RaceId race, int railcar, int place) {
        super(String.format("Place %d on railcar %d on race %s is already reserved", place, railcar, race.id()));
    }
}
