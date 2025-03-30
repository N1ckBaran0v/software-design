package traintickets.businesslogic.service;

import traintickets.businesslogic.api.RaceService;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.TrainAlreadyReservedException;
import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.RaceRepository;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.businesslogic.repository.UserRepository;

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
        race.validate();
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
