package traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.exception.TrainAlreadyReservedException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RaceRepository;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.businesslogic.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RaceServiceImplTest {
    @Mock
    private RaceRepository raceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private RaceServiceImpl raceService;

    @Test
    void addRace_positive_added() throws TrainAlreadyReservedException {
        var start = new Schedule(null, "start", null, Timestamp.valueOf("2001-09-11 08:46:26"), 0);
        var end = new Schedule(null, "end", Timestamp.valueOf("2001-09-11 09:03:02"), null, 100);
        var race = new Race(null, new TrainId(1), List.of(start, end), false);
        raceService.addRace(race);
        verify(raceRepository).addRace(race);
    }

    @Test
    void addRace_negative_invalid() throws TrainAlreadyReservedException {
        var start = new Schedule(null, "start", null, Timestamp.valueOf("2001-09-11 09:03:02"), 0);
        var end = new Schedule(null, "end", Timestamp.valueOf("2001-09-11 08:46:26"), null, 100);
        var race = new Race(null, new TrainId(1), List.of(start, end), false);
        assertThrows(InvalidEntityException.class, () -> raceService.addRace(race));
        verify(raceRepository, never()).addRace(any());
    }

    @Test
    void getRace_positive_found() {
        var start = new Schedule(new ScheduleId(1), "start", null, Timestamp.valueOf("2001-09-11 08:46:26"), 0);
        var end = new Schedule(new ScheduleId(2), "start", null, Timestamp.valueOf("2001-09-11 09:03:02"), 100);
        var raceId = new RaceId(1);
        var race = new Race(raceId, new TrainId(1), List.of(start, end), false);
        given(raceRepository.getRace(raceId)).willReturn(Optional.of(race));
        var result = raceService.getRace(raceId);
        assertSame(race, result);
    }

    @Test
    void getRace_negative_notFound() {
        var raceId = new RaceId(1);
        given(raceRepository.getRace(raceId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> raceService.getRace(raceId));
    }

    @Test
    void finishRace_positive_finished() {
        var start = new Schedule(new ScheduleId(1), "start", null, Timestamp.valueOf("2001-09-11 08:46:26"), 0);
        var end = new Schedule(new ScheduleId(2), "end", Timestamp.valueOf("2001-09-11 09:03:02"), null, 100);
        var raceId = new RaceId(1);
        var race = new Race(raceId, new TrainId(1), List.of(start, end), false);
        given(raceRepository.getRace(raceId)).willReturn(Optional.of(race));
        raceService.finishRace(raceId);
        verify(raceRepository).updateRace(any());
    }

    @Test
    void finishRace_negative_notFound() {
        var raceId = new RaceId(1);
        given(raceRepository.getRace(raceId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> raceService.finishRace(raceId));
        verify(raceRepository, never()).updateRace(any());
    }

    @Test
    void getPassengers_positive_got() {
        var raceId = new RaceId(1);
        var userId1 = new UserId(1);
        var userId2 = new UserId(2);
        var start = new Schedule(new ScheduleId(1), "start", null, Timestamp.valueOf("2001-09-11 08:46:26"), 0);
        var end = new Schedule(new ScheduleId(2), "end", Timestamp.valueOf("2001-09-11 09:03:02"), null, 100);
        var ticket1 = new Ticket(new TicketId(1), userId1, "adult", raceId, 1, new Place(
                new PlaceId(1), 1, null, "any_human", BigDecimal.TEN), start, end, BigDecimal.valueOf(10 * 100));
        var ticket2 = new Ticket(new TicketId(2), userId2, "invalid", raceId, 1, new Place(
                new PlaceId(2), 2, null, "any_human", BigDecimal.TEN), start, end, BigDecimal.valueOf(10 * 100));
        var username1 = "username1";
        var username2 = "username2";
        var user1 = new User(userId1, username1, "qwerty123", "Zubenko Mikhail", "clientRole", true);
        var user2 = new User(userId2, username2, "qwerty123", "Zubenko Mikhail", "clientRole", true);
        given(ticketRepository.getTicketsByRace(raceId)).willReturn(List.of(ticket1, ticket2));
        given(userRepository.getUsers(any())).willReturn(List.of(user1, user2));
        var result = raceService.getPassengers(raceId);
        assertNotNull(result);
        assertEquals(2, result.size());
        var first = result.get(username1);
        assertNotNull(first);
        assertEquals(1, first.size());
        assertEquals("adult", first.getFirst());
        var second = result.get(username2);
        assertNotNull(second);
        assertEquals(1, second.size());
        assertEquals("invalid", second.getFirst());
    }

    @Test
    void getPassengersList_positive_empty() {
        var raceId = new RaceId(1);
        given(ticketRepository.getTicketsByRace(raceId)).willReturn(List.of());
        given(userRepository.getUsers(any())).willReturn(List.of());
        var result = raceService.getPassengers(raceId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}