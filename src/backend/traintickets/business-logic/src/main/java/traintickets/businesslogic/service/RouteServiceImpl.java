package traintickets.businesslogic.service;

import traintickets.businesslogic.api.RouteService;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RaceRepository;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.businesslogic.repository.TrainRepository;

import java.util.*;

public final class RouteServiceImpl implements RouteService {
    private final RailcarRepository railcarRepository;
    private final TrainRepository trainRepository;
    private final RaceRepository raceRepository;
    private final TicketRepository ticketRepository;

    public RouteServiceImpl(RailcarRepository railcarRepository,
                            TrainRepository trainRepository,
                            RaceRepository raceRepository,
                            TicketRepository ticketRepository) {
        this.railcarRepository = Objects.requireNonNull(railcarRepository);
        this.trainRepository = Objects.requireNonNull(trainRepository);
        this.raceRepository = Objects.requireNonNull(raceRepository);
        this.ticketRepository = Objects.requireNonNull(ticketRepository);
    }

    @Override
    public List<Route> getRoutes(Filter filter) {
        filter.validate();
        var races = raceRepository.getRaces(filter);
        var transfers = filter.transfers();
        var map = new HashMap<String, List<Race>>();
        races.forEach(race -> race.schedule().forEach(schedule -> {
            var name = schedule.name();
            if (!map.containsKey(name)) {
                map.put(name, new ArrayList<>());
            }
            map.get(name).add(race);
        }));
        var used = new HashSet<RaceId>();
        var visited = new HashSet<String>();
        visited.add(filter.departure());
        var testRoute = new Route(new ArrayList<>(transfers + 1),
                new ArrayList<>(transfers + 1),
                new ArrayList<>(transfers + 1));
        var result = new ArrayList<Route>();
        var departure = filter.departure();
        if (map.containsKey(departure)) {
            map.get(departure).forEach(race -> searchRoute(result, testRoute, used, visited, filter,
                    map, race, find(race.schedule(), departure, 0), transfers));
        }
        return result;
    }

    private void searchRoute(List<Route> routes,
                             Route testRoute,
                             Set<RaceId> used,
                             Set<String> visited,
                             Filter filter,
                             Map<String, List<Race>> map,
                             Race currentRace,
                             int currentStation,
                             int transfers) {
        used.add(currentRace.id());
        var races = testRoute.races();
        var starts = testRoute.starts();
        var schedule = currentRace.schedule();
        races.add(currentRace.id());
        starts.add(schedule.get(currentStation));
        var dstPos = find(schedule, filter.destination(), currentStation);
        if (dstPos != -1) {
            saveRoute(routes, visited, currentStation, dstPos, schedule, testRoute);
        } else if (transfers > 0) {
            searchNext(routes, testRoute, used, visited, filter, map, currentStation, transfers - 1, schedule);
        }
        starts.removeLast();
        races.removeLast();
        used.remove(currentRace.id());
    }

    private void searchNext(List<Route> routes,
                            Route testRoute,
                            Set<RaceId> used,
                            Set<String> visited,
                            Filter filter,
                            Map<String, List<Race>> map,
                            int currentStation,
                            int transfers,
                            List<Schedule> schedule) {
        var stop = schedule.size();
        for (var i = currentStation + 1; i < stop; ++i) {
            var current = schedule.get(i);
            var name = current.name();
            if (!visited.contains(name)) {
                visited.add(name);
                map.get(name).forEach(race -> {
                    if (!used.contains(race.id())) {
                        testRoute.ends().add(current);
                        var curr = race.schedule();
                        var pos = find(curr, name, 0);
                        var arrivalTime = current.arrival();
                        var departureTime = curr.get(pos).departure();
                        if (arrivalTime != null && departureTime != null && arrivalTime.before(departureTime)) {
                            searchRoute(routes, testRoute, used, visited, filter, map, race, pos, transfers);
                        }
                        testRoute.ends().removeLast();
                    }
                });
            } else {
                stop = i;
            }
        }
        for (var i = currentStation + 1; i < stop; ++i) {
            visited.remove(schedule.get(i).name());
        }
    }

    private void saveRoute(List<Route> routes,
                           Set<String> visited,
                           int currentStation,
                           int dstPos,
                           List<Schedule> schedule,
                           Route testRoute) {
        var stop = dstPos;
        for (var i = currentStation + 1; i < stop; ++i) {
            var station = schedule.get(i).name();
            if (!visited.contains(station)) {
                visited.add(station);
            } else {
                stop = i;
            }
        }
        if (stop == dstPos && dstPos != currentStation) {
            testRoute.ends().add(schedule.get(dstPos));
            var races = new ArrayList<>(testRoute.races());
            var starts = new ArrayList<>(testRoute.starts());
            var ends = new ArrayList<>(testRoute.ends());
            routes.add(new Route(races, starts, ends));
        }
        for (var i = currentStation + 1; i < stop; ++i) {
            visited.remove(schedule.get(i).name());
        }
    }

    private int find(List<Schedule> schedule, String name, int start) {
        var result = -1;
        for (var i = start; i < schedule.size(); ++i) {
            if (name.equals(schedule.get(i).name())) {
                result = i;
                break;
            }
        }
        return result;
    }

    @Override
    public Race getRace(RaceId raceId) {
        return raceRepository.getRace(raceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No race with id %s found", raceId.id())));
    }

    @Override
    public List<List<Place>> getFreePlaces(RaceId raceId) {
        var race = raceRepository.getRace(raceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No race with id %s found", raceId.id())));
        var train = trainRepository.getTrain(race.trainId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("No train with id %s found", race.trainId().id())));
        var numbers = train.railcars();
        var railcars = new HashMap<RailcarId, Railcar>();
        railcarRepository.getRailcarsByTrain(train.id()).forEach(railcar -> railcars.put(railcar.id(), railcar));
        var boughtAll = new ArrayList<Set<Integer>>(numbers.size());
        for (var i = 0; i < numbers.size(); ++i) {
            boughtAll.add(new HashSet<>());
        }
        ticketRepository.getTicketsByRace(raceId).forEach(
                ticket -> boughtAll.get(ticket.railcar() - 1).add(ticket.place().number()));
        var result = new ArrayList<List<Place>>();
        for (var i = 0; i < numbers.size(); ++i) {
            var arr = new ArrayList<Place>();
            var bought = boughtAll.get(i);
            railcars.get(numbers.get(i)).places().forEach(place -> {
                if (!bought.contains(place.number())) {
                    arr.add(place);
                }
            });
            result.add(arr);
        }
        return result;
    }
}
