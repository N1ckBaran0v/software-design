package ru.traintickets.businesslogic.repository;

import ru.traintickets.businesslogic.exception.TrainAlreadyReservedException;
import ru.traintickets.businesslogic.model.Filter;
import ru.traintickets.businesslogic.model.Race;
import ru.traintickets.businesslogic.model.RaceId;

import java.util.Optional;

public interface RaceRepository {
    void addRace(Race race) throws TrainAlreadyReservedException;
    Optional<Race> getRace(RaceId raceId);
    Iterable<Race> getRaces(Filter filter);
    void updateRace(Race race);
    void deleteRace(RaceId raceId);
}
