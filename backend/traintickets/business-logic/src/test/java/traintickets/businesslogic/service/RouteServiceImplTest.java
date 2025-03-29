package traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RaceRepository;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.businesslogic.repository.TrainRepository;

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
class RouteServiceImplTest {
    @Mock
    private RailcarRepository railcarRepository;

    @Mock
    private TrainRepository trainRepository;

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private RouteServiceImpl routeService;

    @Test
    void getRoutes_positive_got() {
        var start1 = new Schedule("start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var end1 = new Schedule("end", Timestamp.valueOf("2025-03-19 11:40:00"), null, 100);
        var race1 = new Race(new RaceId(1), new TrainId(1), List.of(start1, end1), false);
        var start2 = new Schedule("start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var end2 = new Schedule("middle", Timestamp.valueOf("2025-03-19 10:00:00"), null, 50);
        var race2 = new Race(new RaceId(2), new TrainId(1), List.of(start2, end2), false);
        var start3 = new Schedule("middle", null, Timestamp.valueOf("2025-03-19 10:10:00"), 0);
        var end3 = new Schedule("end", Timestamp.valueOf("2025-03-19 11:40:00"), null, 50);
        var race3 = new Race(new RaceId(3), new TrainId(1), List.of(start3, end3), false);
        var filter = new Filter(new UserId(1), "filter", "start", "end", "express", 1, List.of("adult"),
                Timestamp.valueOf("2025-03-19 08:00:00"), Timestamp.valueOf("2025-03-19 12:00:00"),
                BigDecimal.valueOf(50), BigDecimal.valueOf(150));
        given(raceRepository.getRaces(filter)).willReturn(List.of(race1, race2, race3));
        var result = routeService.getRoutes(filter);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getRoutes_positive_empty() {
        var filter = new Filter(new UserId(1), "filter", "start", "end", "express", 1, List.of("adult"),
                Timestamp.valueOf("2025-03-19 08:00:00"), Timestamp.valueOf("2025-03-19 12:00:00"),
                BigDecimal.valueOf(50), BigDecimal.valueOf(150));
        given(raceRepository.getRaces(filter)).willReturn(List.of());
        var result = routeService.getRoutes(filter);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRace_positive_found() {
        var raceId = new RaceId(1);
        var start = new Schedule("start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var end = new Schedule("end", null, Timestamp.valueOf("2025-03-19 10:00:00"), 100);
        var race = new Race(raceId, new TrainId(1), List.of(start, end), false);
        given(raceRepository.getRace(raceId)).willReturn(Optional.of(race));
        var result = routeService.getRace(raceId);
        assertSame(race, result);
    }

    @Test
    void getRace_negative_notFound() {
        var raceId = new RaceId(1);
        given(raceRepository.getRace(raceId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> routeService.getRace(raceId));
    }

    @Test
    void getFreePlaces_positive_got() {
        var raceId = new RaceId(1);
        var trainId = new TrainId(1);
        var railcarId1 = new RailcarId(1);
        var railcarId2 = new RailcarId(2);
        var start = new Schedule("start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var end = new Schedule("end", null, Timestamp.valueOf("2025-03-19 10:00:00"), 100);
        var race = new Race(raceId, trainId, List.of(start, end), false);
        var train = new Train(trainId, "express", List.of(railcarId1, railcarId2));
        var place1 = new Place(1, null, "any_human", BigDecimal.TEN);
        var place2 = new Place(2, null, "any_human", BigDecimal.valueOf(15));
        var railcar1 = new Railcar(railcarId1, "1", "express", List.of(place1));
        var railcar2 = new Railcar(railcarId2, "2", "express", List.of(place1, place2));
        var ticket = new Ticket(new TicketId(1), new UserId(1), "adult",
                raceId, 2, place1, start, end, BigDecimal.valueOf(1000));
        given(raceRepository.getRace(raceId)).willReturn(Optional.of(race));
        given(trainRepository.getTrain(trainId)).willReturn(Optional.of(train));
        given(railcarRepository.getRailcarsByTrain(trainId)).willReturn(List.of(railcar1, railcar2));
        given(ticketRepository.getTicketsByRace(raceId)).willReturn(List.of(ticket));
        var result = routeService.getFreePlaces(raceId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).size());
        assertEquals(place1, result.get(0).getFirst());
        assertEquals(1, result.get(1).size());
        assertEquals(place2, result.get(1).getFirst());
    }

    @Test
    void getFreePlaces_positive_empty() {
        var raceId = new RaceId(1);
        var trainId = new TrainId(1);
        var railcarId1 = new RailcarId(1);
        var railcarId2 = new RailcarId(2);
        var start = new Schedule("start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var end = new Schedule("end", null, Timestamp.valueOf("2025-03-19 10:00:00"), 100);
        var race = new Race(raceId, trainId, List.of(start, end), false);
        var train = new Train(trainId, "express", List.of(railcarId1, railcarId2));
        var place1 = new Place(1, null, "any_human", BigDecimal.TEN);
        var place2 = new Place(2, null, "any_human", BigDecimal.valueOf(15));
        var railcar1 = new Railcar(railcarId1, "1", "express", List.of(place1));
        var railcar2 = new Railcar(railcarId2, "2", "express", List.of(place1, place2));
        var ticket1 = new Ticket(new TicketId(1), new UserId(1), "adult",
                raceId, 1, place1, start, end, BigDecimal.valueOf(1000));
        var ticket2 = new Ticket(new TicketId(2), new UserId(2), "adult",
                raceId, 2, place1, start, end, BigDecimal.valueOf(1000));
        var ticket3 = new Ticket(new TicketId(3), new UserId(3), "adult",
                raceId, 2, place2, start, end, BigDecimal.valueOf(1000));
        given(raceRepository.getRace(raceId)).willReturn(Optional.of(race));
        given(trainRepository.getTrain(trainId)).willReturn(Optional.of(train));
        given(railcarRepository.getRailcarsByTrain(trainId)).willReturn(List.of(railcar1, railcar2));
        given(ticketRepository.getTicketsByRace(raceId)).willReturn(List.of(ticket1, ticket2, ticket3));
        var result = routeService.getFreePlaces(raceId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).isEmpty());
        assertTrue(result.get(1).isEmpty());
    }

    @Test
    void getFreePlaces_negative_notFoundRace() {
        var raceId = new RaceId(1);
        given(raceRepository.getRace(raceId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> routeService.getFreePlaces(raceId));
        verify(trainRepository, never()).getTrain(any());
        verify(railcarRepository, never()).getRailcarsByTrain(any());
        verify(ticketRepository, never()).getTicketsByRace(any());
    }

    @Test
    void getFreePlaces_negative_notFoundTrain() {
        var raceId = new RaceId(1);
        var trainId = new TrainId(1);
        var start = new Schedule("start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var end = new Schedule("end", null, Timestamp.valueOf("2025-03-19 10:00:00"), 100);
        var race = new Race(raceId, trainId, List.of(start, end), false);
        given(raceRepository.getRace(raceId)).willReturn(Optional.of(race));
        given(trainRepository.getTrain(trainId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> routeService.getFreePlaces(raceId));
        verify(railcarRepository, never()).getRailcarsByTrain(any());
        verify(ticketRepository, never()).getTicketsByRace(any());
    }
}