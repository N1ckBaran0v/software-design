package ru.traintickets.businesslogic.service;

import ru.traintickets.businesslogic.api.RaceService;
import ru.traintickets.businesslogic.exception.EntityNotFoundException;
import ru.traintickets.businesslogic.exception.TrainAlreadyReservedException;
import ru.traintickets.businesslogic.model.*;
import ru.traintickets.businesslogic.repository.RaceRepository;
import ru.traintickets.businesslogic.repository.TicketRepository;
import ru.traintickets.businesslogic.repository.UserRepository;

import java.util.*;

public final class RaceServiceImpl implements RaceService {
    private final UserRepository userRepository;
    private final RaceRepository raceRepository;
    private final TicketRepository ticketRepository;

    public RaceServiceImpl(UserRepository userRepository,
                           RaceRepository raceRepository,
                           TicketRepository ticketRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.raceRepository = Objects.requireNonNull(raceRepository);
        this.ticketRepository = Objects.requireNonNull(ticketRepository);
    }

    @Override
    public void addRace(Race race) throws TrainAlreadyReservedException {
        raceRepository.addRace(race);
    }

    @Override
    public Race getRace(RaceId raceId) {
        return raceRepository.getRace(raceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No race with id %d found", raceId.id())));
    }

    @Override
    public void finishRace(RaceId raceId) {
        var race = raceRepository.getRace(raceId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No race with id %d found", raceId.id())));
        var updated = new Race(raceId, race.trainId(), race.schedule(), true);
        raceRepository.updateRace(updated);
    }

    @Override
    public void cancelRace(RaceId raceId) {
        raceRepository.deleteRace(raceId);
    }

    @Override
    public Map<String, List<String>> getPassengers(RaceId raceId) {
        var tickets = new HashMap<UserId, List<String>>();
        ticketRepository.getTicketsByRace(raceId).forEach(ticket -> {
            var key = ticket.owner();
            if (!tickets.containsKey(key)) {
                tickets.put(key, new ArrayList<>());
            }
            tickets.get(key).add(ticket.passenger());
        });
        var result = new HashMap<String, List<String>>();
        userRepository.getUsers(tickets.keySet()).forEach(user -> result.put(user.username(), tickets.get(user.id())));
        return result;
    }
}
