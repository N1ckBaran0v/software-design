package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.exception.PaymentException;
import traintickets.businesslogic.exception.PlaceAlreadyReservedException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.payment.PaymentData;
import traintickets.businesslogic.payment.PaymentManager;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.dataaccess.mongo.model.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TicketRepositoryImplIT extends MongoIT {
    private TicketRepository ticketRepository;

    private MongoCollection<TicketDocument> ticketCollection;
    private MongoCollection<PlaceDocument> placeCollection;
    private MongoCollection<RaceDocument> raceCollection;
    private MongoCollection<TrainDocument> trainCollection;
    private MongoCollection<ScheduleDocument> scheduleCollection;

    private List<ObjectId> placeIds, raceIds, ticketIds, scheduleIds, userIds;
    private ObjectId trainId, railcarId;

    @Mock
    private PaymentManager paymentManager;

    @Mock
    private PaymentData paymentData;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        ticketRepository = new TicketRepositoryImpl(mongoExecutor, paymentManager);
    }

    @AfterEach
    @Override
    void tearDown() {
        super.tearDown();
    }

    @Override
    protected void insertData() {
        ticketCollection = mongoExecutor.getDatabase().getCollection("tickets", TicketDocument.class);
        placeCollection = mongoExecutor.getDatabase().getCollection("places", PlaceDocument.class);
        raceCollection = mongoExecutor.getDatabase().getCollection("races", RaceDocument.class);
        trainCollection = mongoExecutor.getDatabase().getCollection("trains", TrainDocument.class);
        scheduleCollection = mongoExecutor.getDatabase().getCollection("schedules", ScheduleDocument.class);
        mongoExecutor.executeConsumer(session -> {
            railcarId = new ObjectId();
            trainId = Objects.requireNonNull(trainCollection.insertOne(session, new TrainDocument(null, "фирменный",
                    List.of(railcarId, railcarId))).getInsertedId()).asObjectId().getValue();
            var raceMap = raceCollection.insertMany(session, List.of(new RaceDocument(null, trainId, true),
                    new RaceDocument(null, trainId, false))).getInsertedIds();
            raceIds = List.of(raceMap.get(0).asObjectId().getValue(), raceMap.get(1).asObjectId().getValue());
            var scheduleMap = scheduleCollection.insertMany(session, List.of(
                    new ScheduleDocument(null, raceIds.get(0), "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0),
                    new ScheduleDocument(null, raceIds.get(0), "second", Timestamp.valueOf("2025-04-01 10:10:00"), null, 5),
                    new ScheduleDocument(null, raceIds.get(1), "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0),
                    new ScheduleDocument(null, raceIds.get(1), "second", Timestamp.valueOf("2025-04-01 10:10:00"), null, 5)
            )).getInsertedIds();
            scheduleIds = List.of(scheduleMap.get(0).asObjectId().getValue(), scheduleMap.get(1).asObjectId().getValue(),
                    scheduleMap.get(2).asObjectId().getValue(), scheduleMap.get(3).asObjectId().getValue());
            var placeMap = placeCollection.insertMany(session, List.of(
                    new PlaceDocument(null, railcarId, 1, "", "universal", BigDecimal.valueOf(100)),
                    new PlaceDocument(null, railcarId, 2, "", "universal", BigDecimal.valueOf(100))
            )).getInsertedIds();
            placeIds = List.of(placeMap.get(0).asObjectId().getValue(), placeMap.get(1).asObjectId().getValue());
            userIds = List.of(new ObjectId(), new ObjectId());
            var ticketMap = ticketCollection.insertMany(session, List.of(
                    new TicketDocument(null, userIds.get(0), "adult", raceIds.get(0), 1, placeIds.get(0),
                            scheduleIds.get(0), scheduleIds.get(1), BigDecimal.valueOf(500)),
                    new TicketDocument(null, userIds.get(1), "adult", raceIds.get(0), 1, placeIds.get(1),
                            scheduleIds.get(0), scheduleIds.get(1), BigDecimal.valueOf(500)),
                    new TicketDocument(null, userIds.get(0), "adult", raceIds.get(1), 1, placeIds.get(0),
                            scheduleIds.get(2), scheduleIds.get(3), BigDecimal.valueOf(500))
            )).getInsertedIds();
            ticketIds = List.of(ticketMap.get(0).asObjectId().getValue(), ticketMap.get(1).asObjectId().getValue(),
                    ticketMap.get(2).asObjectId().getValue());
        });
    }

    @Test
    void addTickets_positive_added() {
        var userId = new UserId(userIds.getLast().toHexString());
        var raceId = new RaceId(raceIds.getLast().toHexString());
        var placeId = new PlaceId(placeIds.getLast().toHexString());
        var scheduleId1 = new ScheduleId(scheduleIds.get(2).toHexString());
        var scheduleId2 = new ScheduleId(scheduleIds.get(3).toHexString());
        var ticket = new Ticket(null, userId, "adult", raceId, 1,
                new Place(placeId, 2, "", "universal", BigDecimal.valueOf(100)),
                new Schedule(scheduleId1, "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0),
                new Schedule(scheduleId2, "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5),
                BigDecimal.valueOf(500));
        ticketRepository.addTickets(List.of(ticket), paymentData);
        mongoExecutor.executeConsumer(session -> assertEquals(4, ticketCollection.countDocuments(session)));
        verify(paymentManager).pay(paymentData);
    }

    @Test
    void addTickets_negative_finished() {
        var userId = new UserId(userIds.getLast().toHexString());
        var raceId = new RaceId(raceIds.getFirst().toHexString());
        var placeId = new PlaceId(placeIds.getFirst().toHexString());
        var scheduleId1 = new ScheduleId(scheduleIds.get(2).toHexString());
        var scheduleId2 = new ScheduleId(scheduleIds.get(3).toHexString());
        var ticket = new Ticket(null, userId, "adult", raceId, 1,
                new Place(placeId, 2, "", "universal", BigDecimal.valueOf(100)),
                new Schedule(scheduleId1, "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0),
                new Schedule(scheduleId2, "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5),
                BigDecimal.valueOf(500));
        assertThrows(InvalidEntityException.class, () -> ticketRepository.addTickets(List.of(ticket), paymentData));
        verify(paymentManager, never()).pay(any());
        mongoExecutor.executeConsumer(session -> assertEquals(3, ticketCollection.countDocuments(session)));
    }

    @Test
    void addTickets_negative_reserved() {
        var userId = new UserId(userIds.getLast().toHexString());
        var raceId = new RaceId(raceIds.getLast().toHexString());
        var placeId = new PlaceId(placeIds.getFirst().toHexString());
        var scheduleId1 = new ScheduleId(scheduleIds.get(2).toHexString());
        var scheduleId2 = new ScheduleId(scheduleIds.get(3).toHexString());
        var ticket = new Ticket(null, userId, "adult", raceId, 1,
                new Place(placeId, 2, "", "universal", BigDecimal.valueOf(100)),
                new Schedule(scheduleId1, "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0),
                new Schedule(scheduleId2, "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5),
                BigDecimal.valueOf(500));
        assertThrows(PlaceAlreadyReservedException.class, () -> ticketRepository.addTickets(List.of(ticket), paymentData));
        verify(paymentManager, never()).pay(any());
        mongoExecutor.executeConsumer(session -> assertEquals(3, ticketCollection.countDocuments(session)));
    }

    @Test
    void addTickets_negative_paymentRejected() {
        var userId = new UserId(userIds.getLast().toHexString());
        var raceId = new RaceId(raceIds.getLast().toHexString());
        var placeId = new PlaceId(placeIds.getLast().toHexString());
        var scheduleId1 = new ScheduleId(scheduleIds.get(2).toHexString());
        var scheduleId2 = new ScheduleId(scheduleIds.get(3).toHexString());
        var ticket = new Ticket(null, userId, "adult", raceId, 1,
                new Place(placeId, 2, "", "universal", BigDecimal.valueOf(100)),
                new Schedule(scheduleId1, "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0),
                new Schedule(scheduleId2, "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5),
                BigDecimal.valueOf(500));
        willThrow(PaymentException.class).given(paymentManager).pay(paymentData);
        assertThrows(PaymentException.class, () -> ticketRepository.addTickets(List.of(ticket), paymentData));
        mongoExecutor.executeConsumer(session -> assertEquals(3, ticketCollection.countDocuments(session)));
    }

    @Test
    void getTicketsByUser_positive_got() {
        var raceId1 = new RaceId(raceIds.get(0).toHexString());
        var raceId2 = new RaceId(raceIds.get(1).toHexString());
        var userId = new UserId(userIds.getFirst().toHexString());
        var ticketId1 = new TicketId(ticketIds.get(0).toHexString());
        var ticketId2 = new TicketId(ticketIds.get(2).toHexString());
        var placeId = new PlaceId(placeIds.getFirst().toHexString());
        var scheduleId1 = new ScheduleId(scheduleIds.get(0).toHexString());
        var scheduleId2 = new ScheduleId(scheduleIds.get(1).toHexString());
        var scheduleId3 = new ScheduleId(scheduleIds.get(2).toHexString());
        var scheduleId4 = new ScheduleId(scheduleIds.get(3).toHexString());
        var place = new Place(placeId, 1, "", "universal", BigDecimal.valueOf(100));
        var ticket1 = new Ticket(ticketId1, userId, "adult", raceId1, 1, place,
                new Schedule(scheduleId1, "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0),
                new Schedule(scheduleId2, "second", Timestamp.valueOf("2025-04-01 11:40:00"), null, 5),
                BigDecimal.valueOf(500));
        var ticket2 = new Ticket(ticketId2, userId, "adult", raceId2, 1, place,
                new Schedule(scheduleId3, "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0),
                new Schedule(scheduleId4, "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5),
                BigDecimal.valueOf(500));
        var result = ticketRepository.getTicketsByUser(userId);
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        var first = iterator.next();
        assertTrue(iterator.hasNext());
        var second = iterator.next();
        assertFalse(iterator.hasNext());
        if (!ticketId1.equals(first.ticketId())) {
            var temp = first;
            first = second;
            second = temp;
        }
        assertEquals(ticket1.ticketId(), first.ticketId());
        assertEquals(ticket2.ticketId(), second.ticketId());
        assertEquals(ticket1.owner(), first.owner());
        assertEquals(ticket2.owner(), second.owner());
        assertEquals(ticket1.passenger(), first.passenger());
        assertEquals(ticket2.passenger(), second.passenger());
        assertEquals(ticket1.race(), first.race());
        assertEquals(ticket2.race(), second.race());
        assertEquals(ticket1.railcar(), first.railcar());
        assertEquals(ticket2.railcar(), second.railcar());
        assertEquals(ticket1.place(), first.place());
        assertEquals(ticket2.place(), second.place());
        assertEquals(ticket1.start().id(), first.start().id());
        assertEquals(ticket2.start().id(), second.start().id());
        assertEquals(ticket1.cost(), first.cost());
        assertEquals(ticket2.cost(), second.cost());
    }

    @Test
    void getTicketsByUser_positive_empty() {
        var result = ticketRepository.getTicketsByUser(new UserId("123456789012345678901234"));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void getTicketsByRace_positive_got() {
        var raceId = new RaceId(raceIds.getFirst().toHexString());
        var userId1 = new UserId(userIds.get(0).toHexString());
        var userId2 = new UserId(userIds.get(1).toHexString());
        var ticketId1 = new TicketId(ticketIds.get(0).toHexString());
        var ticketId2 = new TicketId(ticketIds.get(1).toHexString());
        var placeId1 = new PlaceId(placeIds.get(0).toHexString());
        var placeId2 = new PlaceId(placeIds.get(1).toHexString());
        var scheduleId1 = new ScheduleId(scheduleIds.get(0).toHexString());
        var scheduleId2 = new ScheduleId(scheduleIds.get(1).toHexString());
        var ticket1 = new Ticket(ticketId1, userId1, "adult", raceId, 1,
                new Place(placeId1, 1, "", "universal", BigDecimal.valueOf(100)),
                new Schedule(scheduleId1, "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0),
                new Schedule(scheduleId2, "second", Timestamp.valueOf("2025-04-01 11:40:00"), null, 5),
                BigDecimal.valueOf(500));
        var ticket2 = new Ticket(ticketId2, userId2, "adult", raceId, 1,
                new Place(placeId2, 2, "", "universal", BigDecimal.valueOf(100)),
                new Schedule(scheduleId1, "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0),
                new Schedule(scheduleId2, "second", Timestamp.valueOf("2025-04-01 11:40:00"), null, 5),
                BigDecimal.valueOf(500));
        var result = ticketRepository.getTicketsByRace(raceId);
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        var first = iterator.next();
        assertTrue(iterator.hasNext());
        var second = iterator.next();
        assertFalse(iterator.hasNext());
        if (!ticketId1.equals(first.ticketId())) {
            var temp = first;
            first = second;
            second = temp;
        }
        assertEquals(ticket1.ticketId(), first.ticketId());
        assertEquals(ticket2.ticketId(), second.ticketId());
        assertEquals(ticket1.owner(), first.owner());
        assertEquals(ticket2.owner(), second.owner());
        assertEquals(ticket1.passenger(), first.passenger());
        assertEquals(ticket2.passenger(), second.passenger());
        assertEquals(ticket1.race(), first.race());
        assertEquals(ticket2.race(), second.race());
        assertEquals(ticket1.railcar(), first.railcar());
        assertEquals(ticket2.railcar(), second.railcar());
        assertEquals(ticket1.place(), first.place());
        assertEquals(ticket2.place(), second.place());
        assertEquals(ticket1.start().id(), first.start().id());
        assertEquals(ticket2.start().id(), second.start().id());
        assertEquals(ticket1.cost(), first.cost());
        assertEquals(ticket2.cost(), second.cost());
    }

    @Test
    void getTicketsByRace_positive_empty() {
        var result = ticketRepository.getTicketsByRace(new RaceId("123456789012345678901234"));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }
}