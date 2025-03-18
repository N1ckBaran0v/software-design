package ru.traintickets.businesslogic.service;

import ru.traintickets.businesslogic.api.RouteService;
import ru.traintickets.businesslogic.exception.EntityNotFoundException;
import ru.traintickets.businesslogic.model.*;
import ru.traintickets.businesslogic.repository.RaceRepository;
import ru.traintickets.businesslogic.repository.RailcarRepository;
import ru.traintickets.businesslogic.repository.TicketRepository;
import ru.traintickets.businesslogic.repository.TrainRepository;

import java.util.*;

public final class RouteServiceImpl implements RouteService {
    static final class RouteNode {
        private final RaceId raceId;
        private final Map<RaceId, RouteNode> children;

        RouteNode(RaceId raceId) {
            this.raceId = raceId;
            this.children = new HashMap<>();
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
    public List<Route> getRoutes(Filter filter) {
        return List.of();
    }

    @Override
    public Race getRace(RaceId raceId) {
        return raceRepository.getRace(raceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No race with id %d found", raceId.id())));
    }

    @Override
    public List<List<Place>> getFreePlaces(RaceId raceId) {
        var race = raceRepository.getRace(raceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No race with id %d found", raceId.id())));
        var train = trainRepository.getTrain(race.trainId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("No train with id %d found", raceId.id())));
        var numbers = train.railcars();
        var railcars = new HashMap<RailcarId, Railcar>();
        railcarRepository.getRailcars(train.id()).forEach(railcar -> railcars.put(railcar.id(), railcar));
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
            for (var place : railcars.get(numbers.get(i)).places()) {
                if (!bought.contains(place.number())) {
                    arr.add(place);
                }
            }
            result.add(arr);
        }
        return result;
    }
}
