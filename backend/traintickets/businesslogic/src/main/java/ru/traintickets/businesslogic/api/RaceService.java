package ru.traintickets.businesslogic.api;

import ru.traintickets.businesslogic.model.Race;
import ru.traintickets.businesslogic.model.RaceId;
import ru.traintickets.businesslogic.model.User;

import java.util.List;

public interface RaceService {
    void addRace(Race race);
    Race getRace(RaceId raceId);
    void finishRace(RaceId raceId);
    void cancelRace(RaceId raceId);
    List<User> getPassengersList(RaceId raceId);
}
