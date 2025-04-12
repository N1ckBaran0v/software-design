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
import java.util.stream.StreamSupport;

public final class RouteServiceImpl implements RouteService {
    final class RaceWrapper {
        private final Race race;
        private final List<RailcarId> numbers;
        private final HashMap<RailcarId, Railcar> railcars;
        private final List<Map<Integer, List<Ticket>>> boughtAll;

        RaceWrapper(Race race) {
            this.race = race;
            var train = trainRepository.getTrain(systemRole, race.trainId()).orElseThrow(
                    () -> new EntityNotFoundException(String.format("No train with id %s found", race.trainId().id())));
            numbers = train.railcars();
            railcars = new HashMap<>();
            railcarRepository.getRailcarsByTrain(systemRole, train.id()).forEach(railcar ->
                    railcars.put(railcar.id(), railcar));
            boughtAll = new ArrayList<>(numbers.size());
            for (var i = 0; i < numbers.size(); ++i) {
                boughtAll.add(new HashMap<>());
            }
            ticketRepository.getTicketsByRace(systemRole, race.id()).forEach(ticket -> {
                var bought = boughtAll.get(ticket.railcar() - 1);
                if (!bought.containsKey(ticket.place().number())) {
                    bought.put(ticket.place().number(), new ArrayList<>());
                }
                bought.get(ticket.place().number()).add(ticket);
            });
        }

        List<List<Place>> getPlaces(Date departure,
                                    Date destination) {
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
    private final String systemRole;

    public RouteServiceImpl(RailcarRepository railcarRepository,
                            TrainRepository trainRepository,
                            RaceRepository raceRepository,
                            TicketRepository ticketRepository,
                            String systemRole) {
        this.railcarRepository = Objects.requireNonNull(railcarRepository);
        this.trainRepository = Objects.requireNonNull(trainRepository);
        this.raceRepository = Objects.requireNonNull(raceRepository);
        this.ticketRepository = Objects.requireNonNull(ticketRepository);
        this.systemRole = Objects.requireNonNull(systemRole);
    }

    @Override
    public List<Route> getRoutes(Filter filter) {
        filter.validate();
        var races = StreamSupport.stream(raceRepository.getRaces(systemRole, filter).spliterator(), false)
                .map(RaceWrapper::new).toList();
        var transfers = filter.transfers();
        var map = new HashMap<String, List<RaceWrapper>>();
        races.forEach(wrapper -> wrapper.race.schedule().forEach(schedule -> {
            var name = schedule.name();
            if (!map.containsKey(name)) {
                map.put(name, new ArrayList<>());
            }
            map.get(name).add(wrapper);
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
            map.get(departure).forEach(wrapper -> searchRoute(result, testRoute, used, visited, filter,
                    map, wrapper, find(wrapper.race.schedule(), departure, 0), transfers));
        }
        return result;
    }

    private void searchRoute(List<Route> routes,
                             Route testRoute,
                             Set<RaceId> used,
                             Set<String> visited,
                             Filter filter,
                             Map<String, List<RaceWrapper>> map,
                             RaceWrapper currentRace,
                             int currentStation,
                             int transfers) {
        used.add(currentRace.race.id());
        var races = testRoute.races();
        var starts = testRoute.starts();
        var schedule = currentRace.race.schedule();
        races.add(currentRace.race.id());
        starts.add(schedule.get(currentStation));
        var dstPos = find(schedule, filter.destination(), currentStation);
        if (dstPos != -1) {
            if (validateRace(filter, currentRace, currentStation, dstPos)) {
                saveRoute(routes, visited, currentStation, dstPos, schedule, testRoute);
            }
        } else if (transfers > 0) {
            searchNext(routes, testRoute, used, visited, filter, map, currentRace, currentStation, transfers - 1);
        }
        starts.removeLast();
        races.removeLast();
        used.remove(currentRace.race.id());
    }

    private void searchNext(List<Route> routes,
                            Route testRoute,
                            Set<RaceId> used,
                            Set<String> visited,
                            Filter filter,
                            Map<String, List<RaceWrapper>> map,
                            RaceWrapper currentRace,
                            int currentStation,
                            int transfers) {
        var schedule = currentRace.race.schedule();
        var stop = schedule.size();
        for (var i = currentStation + 1; i < stop; ++i) {
            var current = schedule.get(i);
            var name = current.name();
            if (!visited.contains(name)) {
                visited.add(name);
                for (var wrapper : map.get(name)) {
                    if (!used.contains(wrapper.race.id()) && validateRace(filter, currentRace, currentStation, i)) {
                        testRoute.ends().add(current);
                        var curr = wrapper.race.schedule();
                        var pos = find(curr, name, 0);
                        var arrivalTime = current.arrival();
                        var departureTime = curr.get(pos).departure();
                        if (arrivalTime != null && departureTime != null && arrivalTime.before(departureTime)) {
                            searchRoute(routes, testRoute, used, visited, filter, map, wrapper, pos, transfers);
                        }
                        testRoute.ends().removeLast();
                    }
                }
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

    private boolean validateRace(Filter filter, RaceWrapper wrapper, int start, int end) {
        var passengers = new HashMap<>(filter.passengers());
        var departure = wrapper.race.schedule().get(start).departure();
        var arrival = wrapper.race.schedule().get(end).arrival();
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

    @Override
    public Race getRace(RaceId raceId) {
        return raceRepository.getRace(systemRole, raceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No race with id %s found", raceId.id())));
    }

    @Override
    public List<List<Place>> getFreePlaces(RaceId raceId, ScheduleId departureId, ScheduleId destinationId) {
        var race = raceRepository.getRace(systemRole, raceId).orElseThrow(
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
