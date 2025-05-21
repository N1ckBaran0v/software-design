package traintickets.businesslogic.service;

import traintickets.businesslogic.api.RouteService;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RaceRepository;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.businesslogic.repository.TrainRepository;

import java.util.*;

public final class RouteServiceImpl implements RouteService {
    final class RaceWrapper {
        private final RaceId raceId;
        private final List<Schedule> schedule;
        private List<RailcarId> numbers;
        private HashMap<RailcarId, Railcar> railcars;
        private List<Map<Integer, List<Ticket>>> boughtAll;

        RaceWrapper(Race race) {
            this.raceId = race.id();
            this.schedule = race.schedule();
            getBought(race);
        }

        private void getBought(Race race) {
            var train = trainRepository.getTrain(race.trainId()).orElseThrow(
                    () -> new EntityNotFoundException(String.format("No train with id %s found", race.trainId().id())));
            numbers = train.railcars();
            railcars = new HashMap<>();
            railcarRepository.getRailcarsByTrain(train.id()).forEach(railcar ->
                    railcars.put(railcar.id(), railcar));
            boughtAll = new ArrayList<>(numbers.size());
            for (var i = 0; i < numbers.size(); ++i) {
                boughtAll.add(new HashMap<>());
            }
            ticketRepository.getTicketsByRace(race.id()).forEach(ticket -> {
                var bought = boughtAll.get(ticket.railcar() - 1);
                if (!bought.containsKey(ticket.place().number())) {
                    bought.put(ticket.place().number(), new ArrayList<>());
                }
                bought.get(ticket.place().number()).add(ticket);
            });
        }

        List<List<Place>> getPlaces(Date departure, Date destination) {
            var result = new ArrayList<List<Place>>();
            for (var i = 0; i < numbers.size(); ++i) {
                var arr = new ArrayList<Place>();
                var bought = boughtAll.get(i);
                for (var place : railcars.get(numbers.get(i)).places()) {
                    var flag = true;
                    for (var ticket : bought.getOrDefault(place.number(), List.of())) {
                        if (!(departure.after(ticket.end().arrival()) || destination.before(ticket.start().departure()))) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        arr.add(place);
                    }
                }
                result.add(arr);
            }
            return result;
        }
    }

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
    public Race getRace(RaceId raceId) {
        return raceRepository.getRace(raceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No race with id %s found", raceId.id())));
    }

    @Override
    public List<Route> getRoutes(Filter filter) {
        filter.searchValidate();
        var races = raceRepository.getRaces(filter).stream()
                .map(RaceWrapper::new).toList();
        var transfers = filter.transfers();
        var result = new ArrayList<Route>();
        if (transfers == 0) {
            getRoutes0(result, races, filter);
        } else {
            getRoutes1(result, races, filter);
        }
        return result;
//        var map = new HashMap<String, List<RaceWrapper>>();
//        races.forEach(wrapper -> wrapper.race.schedule().forEach(schedule -> {
//            var name = schedule.name();
//            if (!map.containsKey(name)) {
//                map.put(name, new ArrayList<>());
//            }
//            map.get(name).add(wrapper);
//        }));
//        var used = new HashSet<RaceId>();
//        var visited = new HashSet<String>();
//        visited.add(filter.departure());
//        var testRoute = new Route(new ArrayList<>(transfers + 1),
//                new ArrayList<>(transfers + 1),
//                new ArrayList<>(transfers + 1));

//        var departure = filter.departure();
//        if (map.containsKey(departure)) {
//            map.get(departure).forEach(wrapper -> searchRoute(result, testRoute, used, visited, filter,
//                    map, wrapper, find(wrapper.race.schedule(), departure, 0), transfers));
//        }
    }

    private void getRoutes0(List<Route> routes, List<RaceWrapper> races, Filter filter) {
        for (var wrapper : races) {
            var departure = findSchedule(wrapper.schedule, filter.departure());
            var destination = findSchedule(wrapper.schedule, filter.destination());
            addDirect(routes, wrapper, filter, departure, destination);
        }
    }

    private void addDirect(List<Route> routes, RaceWrapper wrapper, Filter filter, int departure, int destination) {
        if (departure < destination && validateRace(filter, wrapper, departure, destination)) {
            var raceIds = List.of(wrapper.raceId);
            var departures = List.of(wrapper.schedule.get(departure));
            var destinations = List.of(wrapper.schedule.get(destination));
            routes.add(new Route(raceIds, departures, destinations));
        }
    }

    private void getRoutes1(List<Route> routes, List<RaceWrapper> races, Filter filter) {
        var startPositions = new HashMap<RaceId, Integer>();
        var startSchedule = new HashMap<RaceId, Set<String>>();
        var startWrappers = new HashMap<RaceId, RaceWrapper>();
        var endPositions = new HashMap<RaceId, Integer>();
        var endSchedule = new HashMap<RaceId, Set<String>>();
        var endWrappers = new HashMap<RaceId, RaceWrapper>();
        for (var wrapper : races) {
            var departure = findSchedule(wrapper.schedule, filter.departure());
            var destination = findSchedule(wrapper.schedule, filter.destination());
            if (departure == -1) {
                endPositions.put(wrapper.raceId, destination);
                var set = new HashSet<String>();
                for (var i = 0; i < destination; ++i) {
                    set.add(wrapper.schedule.get(i).name());
                }
                endSchedule.put(wrapper.raceId, set);
                endWrappers.put(wrapper.raceId, wrapper);
            } else if (destination == -1) {
                startPositions.put(wrapper.raceId, departure);
                var set = new HashSet<String>();
                for (var i = departure + 1; i < wrapper.schedule.size(); ++i) {
                    set.add(wrapper.schedule.get(i).name());
                }
                startSchedule.put(wrapper.raceId, set);
                startWrappers.put(wrapper.raceId, wrapper);
            } else {
                addDirect(routes, wrapper, filter, departure, destination);
            }
        }
        for (var start : startPositions.keySet()) {
            for (var end : endPositions.keySet()) {
                var intersection = new HashSet<>(startSchedule.get(start));
                intersection.retainAll(endSchedule.get(end));
                if (!intersection.isEmpty()) {
                    for (var station : intersection) {
                        var wrapper1 = startWrappers.get(start);
                        var wrapper2 = endWrappers.get(end);
                        var schedule1 = wrapper1.schedule;
                        var schedule2 = wrapper2.schedule;
                        var index1 = findSchedule(schedule1, station);
                        var index2 = findSchedule(schedule2, station);
                        var flag = validateRace(filter, wrapper1, 0, index1) &&
                                validateRace(filter, wrapper2, index2, schedule2.size() - 1);
                        if (flag) {
                            var raceIds = List.of(start, end);
                            var departures = List.of(schedule1.get(startPositions.get(start)), schedule2.get(index2));
                            var destinations = List.of(schedule1.get(index1), schedule2.get(endPositions.get(end)));
                            routes.add(new Route(raceIds, departures, destinations));
                            break;
                        }
                    }
                }
            }
        }
    }

//    private void searchRoute(List<Route> routes,
//                             Route testRoute,
//                             Set<RaceId> used,
//                             Set<String> visited,
//                             Filter filter,
//                             Map<String, List<RaceWrapper>> map,
//                             RaceWrapper currentRace,
//                             int currentStation,
//                             int transfers) {
//        used.add(currentRace.race.id());
//        var races = testRoute.races();
//        var starts = testRoute.starts();
//        var schedule = currentRace.race.schedule();
//        races.add(currentRace.race.id());
//        starts.add(schedule.get(currentStation));
//        var dstPos = find(schedule, filter.destination(), currentStation);
//        if (dstPos != -1) {
//            if (validateRace(filter, currentRace, currentStation, dstPos)) {
//                saveRoute(routes, visited, currentStation, dstPos, schedule, testRoute);
//            }
//        } else if (transfers > 0) {
//            searchNext(routes, testRoute, used, visited, filter, map, currentRace, currentStation, transfers - 1);
//        }
//        starts.removeLast();
//        races.removeLast();
//        used.remove(currentRace.race.id());
//    }
//
//    private void searchNext(List<Route> routes,
//                            Route testRoute,
//                            Set<RaceId> used,
//                            Set<String> visited,
//                            Filter filter,
//                            Map<String, List<RaceWrapper>> map,
//                            RaceWrapper currentRace,
//                            int currentStation,
//                            int transfers) {
//        var schedule = currentRace.race.schedule();
//        var stop = schedule.size();
//        for (var i = currentStation + 1; i < stop; ++i) {
//            var current = schedule.get(i);
//            var name = current.name();
//            if (!visited.contains(name)) {
//                visited.add(name);
//                for (var wrapper : map.get(name)) {
//                    if (!used.contains(wrapper.race.id()) && validateRace(filter, currentRace, currentStation, i)) {
//                        testRoute.ends().add(current);
//                        var curr = wrapper.race.schedule();
//                        var pos = find(curr, name, 0);
//                        var arrivalTime = current.arrival();
//                        var departureTime = curr.get(pos).departure();
//                        if (arrivalTime != null && departureTime != null && arrivalTime.before(departureTime)) {
//                            searchRoute(routes, testRoute, used, visited, filter, map, wrapper, pos, transfers);
//                        }
//                        testRoute.ends().removeLast();
//                    }
//                }
//            } else {
//                stop = i;
//            }
//        }
//        for (var i = currentStation + 1; i < stop; ++i) {
//            visited.remove(schedule.get(i).name());
//        }
//    }
//
//    private void saveRoute(List<Route> routes,
//                           Set<String> visited,
//                           int currentStation,
//                           int dstPos,
//                           List<Schedule> schedule,
//                           Route testRoute) {
//        var stop = dstPos;
//        for (var i = currentStation + 1; i < stop; ++i) {
//            var station = schedule.get(i).name();
//            if (!visited.contains(station)) {
//                visited.add(station);
//            } else {
//                stop = i;
//            }
//        }
//        if (stop == dstPos && dstPos != currentStation) {
//            testRoute.ends().add(schedule.get(dstPos));
//            var races = new ArrayList<>(testRoute.races());
//            var starts = new ArrayList<>(testRoute.starts());
//            var ends = new ArrayList<>(testRoute.ends());
//            routes.add(new Route(races, starts, ends));
//        }
//        for (var i = currentStation + 1; i < stop; ++i) {
//            visited.remove(schedule.get(i).name());
//        }
//    }
//
//    private int find(List<Schedule> schedule, String name, int start) {
//        var result = -1;
//        for (var i = start; i < schedule.size(); ++i) {
//            if (name.equals(schedule.get(i).name())) {
//                result = i;
//                break;
//            }
//        }
//        return result;
//    }

    private boolean validateRace(Filter filter, RaceWrapper wrapper, int start, int end) {
        var passengers = new HashMap<>(filter.passengers());
        var departure = wrapper.schedule.get(start).departure();
        var arrival = wrapper.schedule.get(end).arrival();
        for (var railcar : wrapper.getPlaces(departure, arrival)) {
            for (var place : railcar) {
                if (passengers.containsKey(place.purpose())) {
                    passengers.put(place.purpose(), passengers.get(place.purpose()) - 1);
                    if (passengers.get(place.purpose()) == 0) {
                        passengers.remove(place.purpose());
                    }
                }
                if (passengers.isEmpty()) {
                    break;
                }
            }
            if (passengers.isEmpty()) {
                break;
            }
        }
        return passengers.isEmpty();
    }

    private int findSchedule(List<Schedule> schedule, String name) {
        var result = -1;
        for (var i = 0; i < schedule.size(); ++i) {
            if (name.equals(schedule.get(i).name())) {
                result = i;
                break;
            }
        }
        return result;
    }

    @Override
    public List<List<Place>> getFreePlaces(RaceId raceId, ScheduleId departureId, ScheduleId destinationId) {
        var race = raceRepository.getRace(raceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No race with id %s found", raceId.id())));
        var departure = (Date) null;
        var destination = (Date) null;
        for (var station : race.schedule()) {
            if (station.id().equals(departureId)) {
                departure = station.departure();
            } else if (station.id().equals(destinationId)) {
                destination = station.arrival();
            }
        }
        if (departure == null || destination == null || departure.after(destination)) {
            throw new InvalidEntityException(String.format("Invalid schedule for ids %s and %s", departureId,
                    destinationId));
        }
        return new RaceWrapper(race).getPlaces(departure, destination);
    }
}
