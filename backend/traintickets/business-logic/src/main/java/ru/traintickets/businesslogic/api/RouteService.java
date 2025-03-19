package ru.traintickets.businesslogic.api;

import ru.traintickets.businesslogic.model.*;

import java.util.List;

public interface RouteService {
    List<Route> getRoutes(Filter filter);
    Race getRace(RaceId raceId);
    List<List<Place>> getFreePlaces(RaceId raceId);
}
