package ru.traintickets.businesslogic.service;

import ru.traintickets.businesslogic.api.RaceService;
import ru.traintickets.businesslogic.exception.EntityNotFoundException;
import ru.traintickets.businesslogic.model.Race;
import ru.traintickets.businesslogic.model.RaceId;
import ru.traintickets.businesslogic.model.Ticket;
import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.repository.RaceRepository;
import ru.traintickets.businesslogic.repository.TicketRepository;
import ru.traintickets.businesslogic.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

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
    public void addRace(Race race) {
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
    public List<User> getPassengersList(RaceId raceId) {
        return StreamSupport.stream(userRepository.getUsers(
                StreamSupport.stream(ticketRepository.getTicketsByRace(raceId).spliterator(), false)
                        .map(Ticket::owner).distinct().toList())
                .spliterator(), false).toList();
    }
}
