package traintickets.businesslogic.api;

import traintickets.businesslogic.model.*;

import java.util.List;

public interface RouteService {
    List<Route> getRoutes(Filter filter);
    List<List<Place>> getFreePlaces(RaceId raceId, ScheduleId departureId, ScheduleId destinationId);
}
