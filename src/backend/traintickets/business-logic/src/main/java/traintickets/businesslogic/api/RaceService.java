package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;

import java.util.List;
import java.util.Map;

public interface RaceService {
    void addRace(Race race);
    void finishRace(RaceId raceId);
    Map<String, List<String>> getPassengers(RaceId raceId);
}
