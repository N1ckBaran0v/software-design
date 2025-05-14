package traintickets.businesslogic.service;

import traintickets.businesslogic.api.RaceService;
import traintickets.businesslogic.exception.TrainAlreadyReservedException;
import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.RaceRepository;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.businesslogic.transport.UserInfo;

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
    public void addRace(UserInfo userInfo, Race race) throws TrainAlreadyReservedException {
        race.validate();
        var role = userInfo.role();
        raceRepository.addRace(role, race);
    }

    @Override
    public void finishRace(UserInfo userInfo, RaceId raceId) {
        raceRepository.updateRace(userInfo.role(), raceId, true);
    }

    @Override
    public Map<String, List<String>> getPassengers(UserInfo userInfo, RaceId raceId) {
        var role = userInfo.role();
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
