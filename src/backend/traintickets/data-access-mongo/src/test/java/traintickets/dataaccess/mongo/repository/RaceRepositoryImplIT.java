package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
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
import java.util.Arrays;
import java.util.List;

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

//    @Test
//    void addRace_positive_added() {
//        var sched1 = new Schedule(null, "first", null, Timestamp.valueOf("2025-04-01 11:50:00"), 0);
//        var sched2 = new Schedule(null, "second", Timestamp.valueOf("2025-04-01 13:20:00"), null, 5);
//        var race = new Race(null, new TrainId("1"), List.of(sched1, sched2), false);
//        raceRepository.addRace(race);
//        jdbcTemplate.executeCons(Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM races WHERE id = 3;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertTrue(resultSet.next());
//                    assertEquals(race.trainId().id(), String.valueOf(resultSet.getLong("train_id")));
//                    assertFalse(resultSet.getBoolean("finished"));
//                    assertFalse(resultSet.next());
//                }
//            }
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM schedule WHERE race_id = 3;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertTrue(resultSet.next());
//                    assertEquals(sched1.name(), resultSet.getString("station_name"));
//                    assertEquals(sched1.arrival(), resultSet.getTimestamp("arrival"));
//                    assertEquals(sched1.departure(), resultSet.getTimestamp("departure"));
//                    assertEquals(sched1.multiplier(), resultSet.getDouble("multiplier"));
//                    assertTrue(resultSet.next());
//                    assertEquals(sched2.name(), resultSet.getString("station_name"));
//                    assertEquals(sched2.arrival(), resultSet.getTimestamp("arrival"));
//                    assertEquals(sched2.departure(), resultSet.getTimestamp("departure"));
//                    assertEquals(sched2.multiplier(), resultSet.getDouble("multiplier"));
//                    assertFalse(resultSet.next());
//                }
//            }
//        });
//    }
//
//    @Test
//    void addRace_negative_reserved() {
//        var sched1 = new Schedule(new ScheduleId("1"), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0);
//        var sched2 = new Schedule(new ScheduleId("2"), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5);
//        var race = new Race(null, new TrainId("1"), List.of(sched1, sched2), false);
//        assertThrows(TrainAlreadyReservedException.class, () -> raceRepository.addRace(race));
//        jdbcTemplate.executeCons(Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM races WHERE id = 3;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertFalse(resultSet.next());
//                }
//            }
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM schedule WHERE race_id = 3;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertFalse(resultSet.next());
//                }
//            }
//        });
//    }
//
//    @Test
//    void getRace_positive_found() {
//        var id = new RaceId("1");
//        var sched1 = new Schedule(new ScheduleId("1"), "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0);
//        var sched2 = new Schedule(new ScheduleId("2"), "second", Timestamp.valueOf("2025-04-01 11:40:00"), null, 5);
//        var race = new Race(id, new TrainId("1"), List.of(sched1, sched2), true);
//        var result = raceRepository.getRace(id).orElse(null);
//        assertEquals(race, result);
//    }
//
//    @Test
//    void getRace_positive_notFound() {
//        assertNull(raceRepository.getRace(new RaceId("3")).orElse(null));
//    }
//
//    @Test
//    void getRaces_positive_got0() {
//        var filter = new Filter(null, null, "first", "second", 0, null, Timestamp.valueOf("2025-04-01 10:00:00"),
//                Timestamp.valueOf("2025-04-01 12:00:00"));
//        var sched1 = new Schedule(new ScheduleId("3"), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0);
//        var sched2 = new Schedule(new ScheduleId("4"), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5);
//        var race = new Race(new RaceId("2"), new TrainId("2"), List.of(sched1, sched2), false);
//        var result = raceRepository.getRaces(filter);
//        assertNotNull(result);
//        var iterator = result.iterator();
//        assertTrue(iterator.hasNext());
//        assertEquals(race, iterator.next());
//        assertFalse(iterator.hasNext());
//    }
//
//    @Test
//    void getRaces_positive_got1() {
//        var filter = new Filter(null, null, "first", "second", 1, null, Timestamp.valueOf("2025-04-01 10:00:00"),
//                Timestamp.valueOf("2025-04-01 12:00:00"));
//        var sched1 = new Schedule(new ScheduleId("3"), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0);
//        var sched2 = new Schedule(new ScheduleId("4"), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5);
//        var race = new Race(new RaceId("2"), new TrainId("2"), List.of(sched1, sched2), false);
//        var result = raceRepository.getRaces(filter);
//        assertNotNull(result);
//        var iterator = result.iterator();
//        assertTrue(iterator.hasNext());
//        assertEquals(race, iterator.next());
//        assertFalse(iterator.hasNext());
//    }
//
//    @Test
//    void getRaces_positive_got2() {
//        var filter = new Filter(null, null, "first", "second", 2, null, Timestamp.valueOf("2025-04-01 10:00:00"),
//                Timestamp.valueOf("2025-04-01 12:00:00"));
//        var sched1 = new Schedule(new ScheduleId("3"), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0);
//        var sched2 = new Schedule(new ScheduleId("4"), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5);
//        var race = new Race(new RaceId("2"), new TrainId("2"), List.of(sched1, sched2), false);
//        var result = raceRepository.getRaces(filter);
//        assertNotNull(result);
//        var iterator = result.iterator();
//        assertTrue(iterator.hasNext());
//        assertEquals(race, iterator.next());
//        assertFalse(iterator.hasNext());
//    }
//
//    @Test
//    void getRaces_positive_empty() {
//        var filter = new Filter(null, null, "first", "second", 1, null, Timestamp.valueOf("2025-04-01 00:00:00"),
//                Timestamp.valueOf("2025-04-01 11:11:11"));
//        var result = raceRepository.getRaces(filter);
//        assertNotNull(result);
//        assertFalse(result.iterator().hasNext());
//    }
//
//    @Test
//    void updateRace_positive_updates() {
//        var sched1 = new Schedule(new ScheduleId("3"), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0);
//        var sched2 = new Schedule(new ScheduleId("4"), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5);
//        var race = new Race(new RaceId("2"), new TrainId("2"), List.of(sched1, sched2), true);
//        raceRepository.updateRace(race.id(), race.finished());
//        jdbcTemplate.executeCons(Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM races WHERE id = 2;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertTrue(resultSet.next());
//                    assertEquals(race.trainId().id(), String.valueOf(resultSet.getLong("train_id")));
//                    assertTrue(resultSet.getBoolean("finished"));
//                    assertFalse(resultSet.next());
//                }
//            }
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM schedule WHERE race_id = 2;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertTrue(resultSet.next());
//                    assertEquals(sched1.name(), resultSet.getString("station_name"));
//                    assertEquals(sched1.arrival(), resultSet.getTimestamp("arrival"));
//                    assertEquals(sched1.departure(), resultSet.getTimestamp("departure"));
//                    assertEquals(sched1.multiplier(), resultSet.getDouble("multiplier"));
//                    assertTrue(resultSet.next());
//                    assertEquals(sched2.name(), resultSet.getString("station_name"));
//                    assertEquals(sched2.arrival(), resultSet.getTimestamp("arrival"));
//                    assertEquals(sched2.departure(), resultSet.getTimestamp("departure"));
//                    assertEquals(sched2.multiplier(), resultSet.getDouble("multiplier"));
//                    assertFalse(resultSet.next());
//                }
//            }
//        });
//    }
}