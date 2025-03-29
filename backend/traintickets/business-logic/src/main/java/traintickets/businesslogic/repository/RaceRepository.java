package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;

import java.util.Optional;

public interface RaceRepository {
    void addRace(Race race);
    Optional<Race> getRace(RaceId raceId);
    Iterable<Race> getRaces(Filter filter);
    void updateRace(Race race);
}
