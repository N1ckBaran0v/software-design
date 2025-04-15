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
import traintickets.businesslogic.session.SessionManager;

import java.util.*;

public final class RaceServiceImpl implements RaceService {
    private final UserRepository userRepository;
    private final RaceRepository raceRepository;
    private final TicketRepository ticketRepository;
    private final SessionManager sessionManager;

    public RaceServiceImpl(UserRepository userRepository,
                           RaceRepository raceRepository,
                           TicketRepository ticketRepository,
                           SessionManager sessionManager) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.raceRepository = Objects.requireNonNull(raceRepository);
        this.ticketRepository = Objects.requireNonNull(ticketRepository);
        this.sessionManager = Objects.requireNonNull(sessionManager);
    }

    @Override
    public void addRace(String sessionId, Race race) throws TrainAlreadyReservedException {
        race.validate();
        var role = sessionManager.getUserInfo(sessionId).role();
        raceRepository.addRace(role, race);
    }

    @Override
    public void finishRace(String sessionId, RaceId raceId) {
        raceRepository.updateRace(sessionManager.getUserInfo(sessionId).role(), raceId, true);
    }

    @Override
    public Map<String, List<String>> getPassengers(String sessionId, RaceId raceId) {
        var role = sessionManager.getUserInfo(sessionId).role();
        var tickets = new HashMap<UserId, List<String>>();
        ticketRepository.getTicketsByRace(role, raceId).forEach(ticket -> {
            var key = ticket.owner();
            if (!tickets.containsKey(key)) {
                tickets.put(key, new ArrayList<>());
            }
            tickets.get(key).add(ticket.passenger());
        });
        var result = new HashMap<String, List<String>>();
        userRepository.getUsers(role, tickets.keySet()).forEach(
                user -> result.put(user.username(), tickets.get(user.id())));
        return result;
    }
}
