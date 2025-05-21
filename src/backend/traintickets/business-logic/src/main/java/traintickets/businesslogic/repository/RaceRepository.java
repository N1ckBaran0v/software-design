package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;
import traintickets.businesslogic.model.Schedule;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RaceRepository {
    void addRace(Race race);
    Optional<Race> getRace(RaceId raceId);
    List<Race> getRaces(Filter filter);
    void updateRace(RaceId raceId, boolean isFinished);
}
