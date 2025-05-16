package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.exception.TrainAlreadyReservedException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RaceRepository;
import traintickets.dataaccess.mongo.model.RaceDocument;
import traintickets.dataaccess.mongo.model.ScheduleDocument;
import traintickets.dataaccess.mongo.model.TrainDocument;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class RaceRepositoryImplIT extends MongoIT {
    private RaceRepository raceRepository;
    private MongoCollection<TrainDocument> trainCollection;
    private MongoCollection<RaceDocument> raceCollection;
    private MongoCollection<ScheduleDocument> scheduleCollection;

    private List<ObjectId> trainIds, raceIds, scheduleIds;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        raceRepository = new RaceRepositoryImpl(mongoExecutor);
    }

    @AfterEach
    @Override
    void tearDown() {
        super.tearDown();
    }

    @Override
    protected void insertData() {
        trainCollection = mongoExecutor.getDatabase().getCollection("trains", TrainDocument.class);
        raceCollection = mongoExecutor.getDatabase().getCollection("races", RaceDocument.class);
        scheduleCollection = mongoExecutor.getDatabase().getCollection("schedules", ScheduleDocument.class);
        mongoExecutor.executeConsumer(session -> {
            var trainMap = trainCollection.insertMany(session, List.of(
                    new TrainDocument(null, "фирменный", List.of()),
                    new TrainDocument(null, "скорый", List.of())
            )).getInsertedIds();
            trainIds = List.of(trainMap.get(0).asObjectId().getValue(), trainMap.get(1).asObjectId().getValue());
            var raceMap = raceCollection.insertMany(session, List.of(
                    new RaceDocument(null, trainIds.get(0), true),
                    new RaceDocument(null, trainIds.get(1), false)
            )).getInsertedIds();
            raceIds = List.of(raceMap.get(0).asObjectId().getValue(), raceMap.get(1).asObjectId().getValue());
            var scheduleMap = scheduleCollection.insertMany(session, List.of(
                    new ScheduleDocument(null, raceIds.get(0), "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0),
                    new ScheduleDocument(null, raceIds.get(0), "second", Timestamp.valueOf("2025-04-01 11:40:00"), null, 5),
                    new ScheduleDocument(null, raceIds.get(1), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0),
                    new ScheduleDocument(null, raceIds.get(1), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5)
            )).getInsertedIds();
            scheduleIds = List.of(scheduleMap.get(0).asObjectId().getValue(), scheduleMap.get(1).asObjectId().getValue(),
                    scheduleMap.get(2).asObjectId().getValue(), scheduleMap.get(3).asObjectId().getValue());
        });
    }

    @Test
    void addRace_positive_added() {
        var sched1 = new Schedule(null, "first", null, Timestamp.valueOf("2025-04-01 11:50:00"), 0);
        var sched2 = new Schedule(null, "second", Timestamp.valueOf("2025-04-01 13:20:00"), null, 5);
        var race = new Race(null, new TrainId(trainIds.getFirst().toHexString()), List.of(sched1, sched2), false);
        raceRepository.addRace(race);
        mongoExecutor.executeConsumer(session -> {
            assertEquals(3, raceCollection.countDocuments(session));
            assertEquals(6, scheduleCollection.countDocuments(session));
        });
    }

    @Test
    void addRace_negative_reserved() {
        var sched1 = new Schedule(null, "first", null, Timestamp.valueOf("2025-04-01 11:50:00"), 0);
        var sched2 = new Schedule(null, "second", Timestamp.valueOf("2025-04-01 13:20:00"), null, 5);
        var race = new Race(null, new TrainId(trainIds.getLast().toHexString()), List.of(sched1, sched2), false);
        assertThrows(TrainAlreadyReservedException.class, () -> raceRepository.addRace(race));
        mongoExecutor.executeConsumer(session -> {
            assertEquals(2, raceCollection.countDocuments(session));
            assertEquals(4, scheduleCollection.countDocuments(session));
        });
    }

    @Test
    void getRace_positive_found() {
        var raceId = new RaceId(raceIds.getFirst().toHexString());
        var schedId1 = new ScheduleId(scheduleIds.get(0).toHexString());
        var schedId2 = new ScheduleId(scheduleIds.get(1).toHexString());
        var sched1 = new Schedule(schedId1, "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0);
        var sched2 = new Schedule(schedId2, "second", Timestamp.valueOf("2025-04-01 11:40:00"), null, 5);
        var trainId = new TrainId(trainIds.getFirst().toHexString());
        var race = new Race(raceId, trainId, List.of(sched1, sched2), true);
        var result = raceRepository.getRace(raceId).orElse(null);
        assertNotNull(result);
        assertEquals(race.trainId(), result.trainId());
        assertEquals(race.finished(), result.finished());
        assertEquals(race.schedule().size(), result.schedule().size());
        assertEquals(race.schedule().get(0).id(), result.schedule().get(0).id());
        assertEquals(race.schedule().get(1).id(), result.schedule().get(1).id());
    }

    @Test
    void getRace_positive_notFound() {
        assertNull(raceRepository.getRace(new RaceId("123456789012345678901234")).orElse(null));
    }

    @Test
    void getRaces_positive_got0() {
        var filter = new Filter(null, null, "first", "second", 0, null, Timestamp.valueOf("2025-04-01 10:00:00"),
                Timestamp.valueOf("2025-04-01 12:00:00"));
        var raceId = new RaceId(raceIds.getLast().toHexString());
        var result = raceRepository.getRaces(filter);
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(raceId, iterator.next().id());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getRaces_positive_got1() {
        var filter = new Filter(null, null, "first", "second", 1, null, Timestamp.valueOf("2025-04-01 10:00:00"),
                Timestamp.valueOf("2025-04-01 12:00:00"));
        var raceId = new RaceId(raceIds.getLast().toHexString());
        var result = raceRepository.getRaces(filter);
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(raceId, iterator.next().id());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getRaces_positive_got2() {
        var filter = new Filter(null, null, "first", "second", 2, null, Timestamp.valueOf("2025-04-01 10:00:00"),
                Timestamp.valueOf("2025-04-01 12:00:00"));
        var raceId = new RaceId(raceIds.getLast().toHexString());
        var result = raceRepository.getRaces(filter);
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(raceId, iterator.next().id());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getRaces_positive_empty() {
        var filter = new Filter(null, null, "first", "second", 1, null, Timestamp.valueOf("2025-04-01 00:00:00"),
                Timestamp.valueOf("2025-04-01 11:11:11"));
        var result = raceRepository.getRaces(filter);
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void updateRace_positive_updates() {
        raceRepository.updateRace(new RaceId(raceIds.getFirst().toHexString()), false);
        mongoExecutor.executeConsumer(session -> {
            assertEquals(2, raceCollection.countDocuments(session));
            assertEquals(4, scheduleCollection.countDocuments(session));
            assertFalse(Objects.requireNonNull(raceCollection
                    .find(session, Filters.eq("_id", raceIds.getFirst())).first()).finished());
        });
    }

    @Test
    void updateRace_positive_notFound() {
        raceRepository.updateRace(new RaceId("123456789012345678901234"), false);
        mongoExecutor.executeConsumer(session -> {
            assertEquals(2, raceCollection.countDocuments(session));
            assertEquals(4, scheduleCollection.countDocuments(session));
        });
    }
}