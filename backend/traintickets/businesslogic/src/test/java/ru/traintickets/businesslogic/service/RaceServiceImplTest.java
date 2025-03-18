package ru.traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.traintickets.businesslogic.exception.EntityNotFoundException;
import ru.traintickets.businesslogic.exception.TrainAlreadyReservedException;
import ru.traintickets.businesslogic.model.*;
import ru.traintickets.businesslogic.repository.RaceRepository;
import ru.traintickets.businesslogic.repository.TicketRepository;
import ru.traintickets.businesslogic.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
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
        var start = new Schedule("start", null, Timestamp.valueOf("2001-09-11 08:46:26"), 0);
        var end = new Schedule("start", null, Timestamp.valueOf("2001-09-11 09:03:02"), 100);
        var race = new Race(null, new TrainId(1), List.of(start, end), false);
        raceService.addRace(race);
        verify(raceRepository).addRace(race);
    }

    @Test
    void addRace_negative_reserved() throws TrainAlreadyReservedException {
        var start = new Schedule("start", null, Timestamp.valueOf("2001-09-11 08:46:26"), 0);
        var end = new Schedule("start", null, Timestamp.valueOf("2001-09-11 09:03:02"), 100);
        var trainId = new TrainId(1);
        var race = new Race(null, trainId, List.of(start, end), false);
        var exception = new TrainAlreadyReservedException(trainId);
        willThrow(exception).given(raceRepository).addRace(race);
        assertThrows(TrainAlreadyReservedException.class, () -> raceService.addRace(race));
    }

    @Test
    void getRace_positive_found() {
        var start = new Schedule("start", null, Timestamp.valueOf("2001-09-11 08:46:26"), 0);
        var end = new Schedule("start", null, Timestamp.valueOf("2001-09-11 09:03:02"), 100);
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
        var start = new Schedule("start", null, Timestamp.valueOf("2001-09-11 08:46:26"), 0);
        var end = new Schedule("start", null, Timestamp.valueOf("2001-09-11 09:03:02"), 100);
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
    void cancelRace_positive_cancelled() {
        var raceId = new RaceId(1);
        raceService.cancelRace(raceId);
        verify(raceRepository).deleteRace(any());
    }

    @Test
    void getPassengersList_positive_got() {
        var raceId = new RaceId(1);
        var userId1 = new UserId("first_user");
        var userId2 = new UserId("second_user");
        var start = new Schedule("start", null, Timestamp.valueOf("2001-09-11 08:46:26"), 0);
        var end = new Schedule("start", null, Timestamp.valueOf("2001-09-11 09:03:02"), 100);
        var ticket1 = new Ticket(userId1, raceId, 1,
                new Place(1, null, "any_human", BigDecimal.TEN), start, end, BigDecimal.valueOf(10 * 100));
        var ticket2 = new Ticket(userId2, raceId, 1,
                new Place(2, null, "any_human", BigDecimal.TEN), start, end, BigDecimal.valueOf(10 * 100));
        var user1 = new User(userId1, "qwerty123", "Zubenko Mikhail", "clientRole", true);
        var user2 = new User(userId2, "qwerty123", "Zubenko Mikhail", "clientRole", true);
        given(ticketRepository.getTicketsByRace(raceId)).willReturn(List.of(ticket1, ticket2));
        given(userRepository.getUsers(any())).willReturn(List.of(user1, user2));
        var result = raceService.getPassengersList(raceId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(user1, result.get(0));
        assertEquals(user2, result.get(1));
    }

    @Test
    void getPassengersList_positive_empty() {
        var raceId = new RaceId(1);
        given(ticketRepository.getTicketsByRace(raceId)).willReturn(List.of());
        given(userRepository.getUsers(any())).willReturn(List.of());
        var result = raceService.getPassengersList(raceId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}