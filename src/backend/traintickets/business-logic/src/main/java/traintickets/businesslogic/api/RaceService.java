package traintickets.businesslogic.api;

import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;
import traintickets.businesslogic.transport.UserInfo;

import java.util.List;
import java.util.Map;

public interface RaceService {
    void addRace(UserInfo userInfo, Race race);
    void finishRace(UserInfo userInfo, RaceId raceId);
    Map<String, List<String>> getPassengers(UserInfo userInfo, RaceId raceId);
}
