package ru.traintickets.businesslogic.api;

import ru.traintickets.businesslogic.exception.TrainAlreadyReservedException;
import ru.traintickets.businesslogic.model.Race;
import ru.traintickets.businesslogic.model.RaceId;

import java.util.List;
import java.util.Map;

public interface RaceService {
    void addRace(Race race) throws TrainAlreadyReservedException;
    Race getRace(RaceId raceId);
    void finishRace(RaceId raceId);
    void cancelRace(RaceId raceId);
    Map<String, List<String>> getPassengers(RaceId raceId);
}
