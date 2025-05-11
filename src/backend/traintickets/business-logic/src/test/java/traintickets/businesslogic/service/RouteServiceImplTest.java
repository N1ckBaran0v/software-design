package traintickets.businesslogic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RaceRepository;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.businesslogic.repository.TrainRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
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

    private final String systemRole = "system_role";
    private RouteServiceImpl routeService;

    @BeforeEach
    void setUp() {
        routeService = new RouteServiceImpl(railcarRepository, trainRepository,
                raceRepository, ticketRepository, systemRole);
    }

    @Test
    void getRoutes_positive_got0() {
        var start1 = new Schedule(new ScheduleId("1"), "start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var end1 = new Schedule(new ScheduleId("2"), "end", Timestamp.valueOf("2025-03-19 11:40:00"), null, 100);
        var race1 = new Race(new RaceId("1"), new TrainId("1"), List.of(start1, end1), false);
        var start4 = new Schedule(new ScheduleId("1"), "start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var end4 = new Schedule(new ScheduleId("2"), "end", Timestamp.valueOf("2025-03-19 11:40:00"), null, 100);
        var race4 = new Race(new RaceId("4"), new TrainId("4"), List.of(start1, end1), false);
        var filter = new Filter(new UserId("1"), "filter", "start", "end", 0, Map.of("adult", 2),
                Timestamp.valueOf("2025-03-19 08:00:00"), Timestamp.valueOf("2025-03-19 12:00:00"));
        var railcarId1 = new RailcarId("1");
        var railcarId2 = new RailcarId("2");
        var place1 = new Place(new PlaceId("1"), 1, "", "adult", BigDecimal.TEN);
        var place2 = new Place(new PlaceId("2"), 2, "", "adult", BigDecimal.TEN);
        var place3 = new Place(new PlaceId("3"), 1, "", "adult", BigDecimal.TEN);
        var railcar1 = new Railcar(railcarId1, "1", "coupe", List.of(place1, place2));
        var railcar2 = new Railcar(railcarId2, "2", "coupe", List.of(place3));
        var train1 = new Train(new TrainId("1"), "express", List.of(railcarId1));
        var train4 = new Train(new TrainId("4"), "express", List.of(railcarId1, railcarId2));
        var ticket1 = new Ticket(new TicketId("1"), new UserId("1"), "adult",
                new RaceId("4"), 1, place1, start4, end4, BigDecimal.valueOf(500));
        var ticket2 = new Ticket(new TicketId("2"), new UserId("2"), "adult",
                new RaceId("4"), 2, place3, start4, end4, BigDecimal.valueOf(500));
        given(raceRepository.getRaces(systemRole, filter)).willReturn(
                Map.of(race1.id(), race1.schedule(), race4.id(), race4.schedule()));
        given(raceRepository.getRace(systemRole, race1.id())).willReturn(Optional.of(race1));
        given(raceRepository.getRace(systemRole, race4.id())).willReturn(Optional.of(race4));
        given(trainRepository.getTrain(systemRole, train1.id())).willReturn(Optional.of(train1));
        given(trainRepository.getTrain(systemRole, train4.id())).willReturn(Optional.of(train4));
        given(railcarRepository.getRailcarsByTrain(systemRole, train1.id())).willReturn(List.of(railcar1));
        given(railcarRepository.getRailcarsByTrain(systemRole, train4.id())).willReturn(List.of(railcar1, railcar2));
        given(ticketRepository.getTicketsByRace(systemRole, race1.id())).willReturn(List.of());
        given(ticketRepository.getTicketsByRace(systemRole, race4.id())).willReturn(List.of(ticket1, ticket2));
        var result = routeService.getRoutes(filter);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRoutes_positive_got1() {
        var start1 = new Schedule(new ScheduleId("1"), "start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var end1 = new Schedule(new ScheduleId("2"), "end", Timestamp.valueOf("2025-03-19 11:40:00"), null, 100);
        var race1 = new Race(new RaceId("1"), new TrainId("1"), List.of(start1, end1), false);
        var start2 = new Schedule(new ScheduleId("3"), "start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var end2 = new Schedule(new ScheduleId("4"), "middle", Timestamp.valueOf("2025-03-19 10:00:00"), null, 50);
        var race2 = new Race(new RaceId("2"), new TrainId("2"), List.of(start2, end2), false);
        var start3 = new Schedule(new ScheduleId("5"), "middle", null, Timestamp.valueOf("2025-03-19 10:10:00"), 0);
        var end3 = new Schedule(new ScheduleId("6"), "end", Timestamp.valueOf("2025-03-19 11:40:00"), null, 50);
        var race3 = new Race(new RaceId("3"), new TrainId("3"), List.of(start3, end3), false);
        var start4 = new Schedule(new ScheduleId("1"), "start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var end4 = new Schedule(new ScheduleId("2"), "end", Timestamp.valueOf("2025-03-19 11:40:00"), null, 100);
        var race4 = new Race(new RaceId("4"), new TrainId("4"), List.of(start1, end1), false);
        var filter = new Filter(new UserId("1"), "filter", "start", "end", 1, Map.of("adult", 2),
                Timestamp.valueOf("2025-03-19 08:00:00"), Timestamp.valueOf("2025-03-19 12:00:00"));
        var railcarId1 = new RailcarId("1");
        var railcarId2 = new RailcarId("2");
        var place1 = new Place(new PlaceId("1"), 1, "", "adult", BigDecimal.TEN);
        var place2 = new Place(new PlaceId("2"), 2, "", "adult", BigDecimal.TEN);
        var place3 = new Place(new PlaceId("3"), 1, "", "adult", BigDecimal.TEN);
        var railcar1 = new Railcar(railcarId1, "1", "coupe", List.of(place1, place2));
        var railcar2 = new Railcar(railcarId2, "2", "coupe", List.of(place3));
        var train1 = new Train(new TrainId("1"), "express", List.of(railcarId1));
        var train2 = new Train(new TrainId("2"), "express", List.of(railcarId1));
        var train3 = new Train(new TrainId("3"), "express", List.of(railcarId1));
        var train4 = new Train(new TrainId("4"), "express", List.of(railcarId1, railcarId2));
        var ticket1 = new Ticket(new TicketId("1"), new UserId("1"), "adult",
                new RaceId("4"), 1, place1, start4, end4, BigDecimal.valueOf(500));
        var ticket2 = new Ticket(new TicketId("2"), new UserId("2"), "adult",
                new RaceId("4"), 2, place3, start4, end4, BigDecimal.valueOf(500));
        given(raceRepository.getRaces(systemRole, filter)).willReturn(
                Map.of(race1.id(), race1.schedule(), race2.id(), race2.schedule(),
                        race3.id(), race3.schedule(), race4.id(), race4.schedule()));
        given(raceRepository.getRace(systemRole, race1.id())).willReturn(Optional.of(race1));
        given(raceRepository.getRace(systemRole, race2.id())).willReturn(Optional.of(race2));
        given(raceRepository.getRace(systemRole, race3.id())).willReturn(Optional.of(race3));
        given(raceRepository.getRace(systemRole, race4.id())).willReturn(Optional.of(race4));
        given(trainRepository.getTrain(systemRole, train1.id())).willReturn(Optional.of(train1));
        given(trainRepository.getTrain(systemRole, train2.id())).willReturn(Optional.of(train2));
        given(trainRepository.getTrain(systemRole, train3.id())).willReturn(Optional.of(train3));
        given(trainRepository.getTrain(systemRole, train4.id())).willReturn(Optional.of(train4));
        given(railcarRepository.getRailcarsByTrain(systemRole, train1.id())).willReturn(List.of(railcar1));
        given(railcarRepository.getRailcarsByTrain(systemRole, train2.id())).willReturn(List.of(railcar1));
        given(railcarRepository.getRailcarsByTrain(systemRole, train3.id())).willReturn(List.of(railcar1));
        given(railcarRepository.getRailcarsByTrain(systemRole, train4.id())).willReturn(List.of(railcar1, railcar2));
        given(ticketRepository.getTicketsByRace(systemRole, race1.id())).willReturn(List.of());
        given(ticketRepository.getTicketsByRace(systemRole, race2.id())).willReturn(List.of());
        given(ticketRepository.getTicketsByRace(systemRole, race3.id())).willReturn(List.of());
        given(ticketRepository.getTicketsByRace(systemRole, race4.id())).willReturn(List.of(ticket1, ticket2));
        var result = routeService.getRoutes(filter);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getRoutes_positive_empty() {
        var filter = new Filter(new UserId("1"), "filter", "start", "end", 1, Map.of("adult", 1),
                Timestamp.valueOf("2025-03-19 08:00:00"), Timestamp.valueOf("2025-03-19 12:00:00"));
        given(raceRepository.getRaces(systemRole, filter)).willReturn(Map.of());
        var result = routeService.getRoutes(filter);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRoutes_negative_invalidFilter() {
        var filter = new Filter(new UserId("1"), "filter", "start", "start", 1, Map.of("adult", 1),
                Timestamp.valueOf("2025-03-19 08:00:00"), Timestamp.valueOf("2025-03-19 12:00:00"));
        assertThrows(InvalidEntityException.class, () -> routeService.getRoutes(filter));
        verify(raceRepository, never()).getRaces(any(), any());
    }

    @Test
    void getRace_positive_found() {
        var start = new Schedule(new ScheduleId("1"), "start", null, Timestamp.valueOf("2001-09-11 08:46:26"), 0);
        var end = new Schedule(new ScheduleId("2"), "start", null, Timestamp.valueOf("2001-09-11 09:03:02"), 100);
        var raceId = new RaceId("1");
        var race = new Race(raceId, new TrainId("1"), List.of(start, end), false);
        given(raceRepository.getRace(systemRole, raceId)).willReturn(Optional.of(race));
        var result = routeService.getRace(raceId);
        assertSame(race, result);
    }

    @Test
    void getRace_negative_notFound() {
        var raceId = new RaceId("1");
        given(raceRepository.getRace(systemRole, raceId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> routeService.getRace(raceId));
    }

    @Test
    void getFreePlaces_positive_got() {
        var raceId = new RaceId("1");
        var trainId = new TrainId("1");
        var railcarId1 = new RailcarId("1");
        var railcarId2 = new RailcarId("2");
        var start = new Schedule(new ScheduleId("1"), "start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var middle = new Schedule(new ScheduleId("2"), "middle", Timestamp.valueOf("2025-03-19 10:00:00"),
                Timestamp.valueOf("2025-03-19 10:10:00"), 50);
        var end = new Schedule( new ScheduleId("3"), "end", Timestamp.valueOf("2025-03-19 11:40:00"), null, 100);
        var race = new Race(raceId, trainId, List.of(start, middle, end), false);
        var train = new Train(trainId, "express", List.of(railcarId1, railcarId2));
        var place1 = new Place(new PlaceId("1"), 1, "", "adult", BigDecimal.TEN);
        var place2 = new Place(new PlaceId("2"), 2, "", "adult", BigDecimal.TEN);
        var place3 = new Place(new PlaceId("3"), 1, "", "adult", BigDecimal.TEN);
        var railcar1 = new Railcar(railcarId1, "1", "coupe", List.of(place1, place2));
        var railcar2 = new Railcar(railcarId2, "2", "coupe", List.of(place3));
        var ticket1 = new Ticket(new TicketId("1"), new UserId("1"), "adult",
                raceId, 1, place1, start, middle, BigDecimal.valueOf(500));
        var ticket2 = new Ticket(new TicketId("2"), new UserId("2"), "adult",
                raceId, 2, place3, middle, end, BigDecimal.valueOf(500));
        given(raceRepository.getRace(systemRole, raceId)).willReturn(Optional.of(race));
        given(trainRepository.getTrain(systemRole, trainId)).willReturn(Optional.of(train));
        given(railcarRepository.getRailcarsByTrain(systemRole, trainId)).willReturn(List.of(railcar1, railcar2));
        given(ticketRepository.getTicketsByRace(systemRole, raceId)).willReturn(List.of(ticket1, ticket2));
        var result = routeService.getFreePlaces(raceId, start.id(), middle.id());
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).size());
        assertEquals(place2, result.get(0).getFirst());
        assertEquals(1, result.get(1).size());
        assertEquals(place3, result.get(1).getFirst());
    }

    @Test
    void getFreePlaces_positive_empty() {
        var raceId = new RaceId("1");
        var trainId = new TrainId("1");
        var railcarId1 = new RailcarId("1");
        var railcarId2 = new RailcarId("2");
        var start = new Schedule(new ScheduleId("1"), "start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var middle = new Schedule(new ScheduleId("2"), "middle", Timestamp.valueOf("2025-03-19 10:00:00"),
                Timestamp.valueOf("2025-03-19 10:10:00"), 50);
        var end = new Schedule( new ScheduleId("3"), "end", Timestamp.valueOf("2025-03-19 11:40:00"), null, 100);
        var race = new Race(raceId, trainId, List.of(start, middle, end), false);
        var train = new Train(trainId, "express", List.of(railcarId1, railcarId2));
        var place1 = new Place(new PlaceId("1"), 1, "", "adult", BigDecimal.TEN);
        var place2 = new Place(new PlaceId("2"), 2, "", "adult", BigDecimal.TEN);
        var place3 = new Place(new PlaceId("3"), 1, "", "adult", BigDecimal.TEN);
        var railcar1 = new Railcar(railcarId1, "1", "coupe", List.of(place1, place2));
        var railcar2 = new Railcar(railcarId2, "2", "coupe", List.of(place3));
        var ticket1 = new Ticket(new TicketId("1"), new UserId("1"), "adult",
                raceId, 1, place1, start, middle, BigDecimal.valueOf(500));
        var ticket2 = new Ticket(new TicketId("2"), new UserId("2"), "adult",
                raceId, 1, place2, start, end, BigDecimal.valueOf(500));
        var ticket3 = new Ticket(new TicketId("3"), new UserId("3"), "adult",
                raceId, 2, place3, start, end, BigDecimal.valueOf(500));
        given(raceRepository.getRace(systemRole, raceId)).willReturn(Optional.of(race));
        given(trainRepository.getTrain(systemRole, trainId)).willReturn(Optional.of(train));
        given(railcarRepository.getRailcarsByTrain(systemRole, trainId)).willReturn(List.of(railcar1, railcar2));
        given(ticketRepository.getTicketsByRace(systemRole, raceId)).willReturn(List.of(ticket1, ticket2, ticket3));
        var result = routeService.getFreePlaces(raceId, start.id(), middle.id());
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).isEmpty());
        assertTrue(result.get(1).isEmpty());
    }


    @Test
    void getFreePlaces_negative_raceNotFound() {
        var raceId = new RaceId("1");
        given(raceRepository.getRace(systemRole, raceId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> routeService.getFreePlaces(raceId, new ScheduleId("1"), new ScheduleId("2")));
        verify(trainRepository, never()).getTrain(any(), any());
        verify(railcarRepository, never()).getRailcarsByTrain(any(), any());
        verify(ticketRepository, never()).getTicketsByRace(any(), any());
    }

    @Test
    void getFreePlaces_negative_invalidSchedule() {
        var raceId = new RaceId("1");
        var trainId = new TrainId("1");
        var start = new Schedule(new ScheduleId("1"), "start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var middle = new Schedule(new ScheduleId("2"), "middle", Timestamp.valueOf("2025-03-19 10:00:00"),
                Timestamp.valueOf("2025-03-19 10:10:00"), 50);
        var end = new Schedule( new ScheduleId("3"), "end", Timestamp.valueOf("2025-03-19 11:40:00"), null, 100);
        var race = new Race(raceId, trainId, List.of(start, middle, end), false);
        given(raceRepository.getRace(systemRole, raceId)).willReturn(Optional.of(race));
        assertThrows(InvalidEntityException.class, () -> routeService.getFreePlaces(raceId, end.id(), start.id()));
        verify(trainRepository, never()).getTrain(any(), any());
        verify(railcarRepository, never()).getRailcarsByTrain(any(), any());
        verify(ticketRepository, never()).getTicketsByRace(any(), any());
    }

    @Test
    void getFreePlaces_negative_trainNotFound() {
        var raceId = new RaceId("1");
        var trainId = new TrainId("1");
        var start = new Schedule(new ScheduleId("1"), "start", null, Timestamp.valueOf("2025-03-19 08:30:00"), 0);
        var middle = new Schedule(new ScheduleId("2"), "middle", Timestamp.valueOf("2025-03-19 10:00:00"),
                Timestamp.valueOf("2025-03-19 10:10:00"), 50);
        var end = new Schedule( new ScheduleId("3"), "end", Timestamp.valueOf("2025-03-19 11:40:00"), null, 100);
        var race = new Race(raceId, trainId, List.of(start, middle, end), false);
        given(raceRepository.getRace(systemRole, raceId)).willReturn(Optional.of(race));
        given(trainRepository.getTrain(systemRole, trainId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> routeService.getFreePlaces(raceId, start.id(), middle.id()));
        verify(railcarRepository, never()).getRailcarsByTrain(any(), any());
        verify(ticketRepository, never()).getTicketsByRace(any(), any());
    }
}