package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;

import java.util.Optional;

public interface RaceRepository {
    void addRace(String role, Race race);
    Optional<Race> getRace(String role, RaceId raceId);
    Iterable<Race> getRaces(String role, Filter filter);
    void updateRace(String role, RaceId raceId, boolean isFinished);
}
