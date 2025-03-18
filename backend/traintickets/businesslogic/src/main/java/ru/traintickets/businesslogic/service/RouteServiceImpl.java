package ru.traintickets.businesslogic.service;

import ru.traintickets.businesslogic.api.RouteService;
import ru.traintickets.businesslogic.exception.EntityNotFoundException;
import ru.traintickets.businesslogic.model.*;
import ru.traintickets.businesslogic.repository.RaceRepository;
import ru.traintickets.businesslogic.repository.RailcarRepository;
import ru.traintickets.businesslogic.repository.TicketRepository;
import ru.traintickets.businesslogic.repository.TrainRepository;

import java.util.List;
import java.util.Objects;

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
        var railcars = railcarRepository.getRailcars(train.id());
        var tickets = ticketRepository.getTicketsByRace(raceId);

        return List.of();
    }
}
