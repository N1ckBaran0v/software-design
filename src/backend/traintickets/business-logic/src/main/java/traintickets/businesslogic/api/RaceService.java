package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;

import java.util.List;
import java.util.Map;

public interface RaceService {
    void addRace(String sessionId, Race race);
    void finishRace(String sessionId, RaceId raceId);
    Map<String, List<String>> getPassengers(String sessionId, RaceId raceId);
}
