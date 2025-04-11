//package traintickets.dataaccess.repository;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import traintickets.businesslogic.exception.TrainAlreadyReservedException;
//import traintickets.businesslogic.model.*;
//import traintickets.businesslogic.repository.RaceRepository;
//
//import java.sql.Connection;
//import java.sql.Timestamp;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class RaceRepositoryImplIT extends PostgresIT {
//    private RaceRepository raceRepository;
//
//    @BeforeEach
//    @Override
//    void setUp() {
//        super.setUp();
//        raceRepository = new RaceRepositoryImpl(jdbcTemplate);
//    }
//
//    @Override
//    protected void insertData() {
//        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "insert into trains (train_class) values ('фирменный'), ('Скорый'); " +
//                            "insert into races (train_id, finished) values " +
//                            "(1, true), (2, false); " +
//                            "insert into schedule (race_id, station_name, arrival, departure, multiplier) values " +
//                            "(1, 'first', null, '2025-04-01 10:10:00+03', 0), " +
//                            "(1, 'second', '2025-04-01 11:40:00+03', null, 5), " +
//                            "(2, 'first', null, '2025-04-01 11:00:00+03', 0), " +
//                            "(2, 'second', '2025-04-01 12:00:00+03', null, 5); " +
//                            "insert into railcars (railcar_model, railcar_type) values (1, 'сидячий'); " +
//                            "insert into railcarsintrains (train_id, railcar_id) values (1, 1); "
//            )) {
//                statement.execute();
//            }
//        });
//    }
//
//    @Test
//    void addRace_positive_added() {
//        var sched1 = new Schedule(null, "first", null, Timestamp.valueOf("2025-04-01 11:50:00"), 0);
//        var sched2 = new Schedule(null, "second", Timestamp.valueOf("2025-04-01 13:20:00"), null, 5);
//        var race = new Race(null, new TrainId(1L), List.of(sched1, sched2), false);
//        raceRepository.addRace(roleName, race);
//        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM races WHERE id = 3;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertTrue(resultSet.next());
//                    assertEquals(race.trainId().id(), resultSet.getLong("train_id"));
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
//        var sched1 = new Schedule(new ScheduleId(1L), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0);
//        var sched2 = new Schedule(new ScheduleId(2L), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5);
//        var race = new Race(null, new TrainId(1L), List.of(sched1, sched2), false);
//        assertThrows(TrainAlreadyReservedException.class, () -> raceRepository.addRace(roleName, race));
//        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
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
//        var id = new RaceId(1L);
//        var sched1 = new Schedule(new ScheduleId(1L), "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0);
//        var sched2 = new Schedule(new ScheduleId(2L), "second", Timestamp.valueOf("2025-04-01 11:40:00"), null, 5);
//        var race = new Race(id, new TrainId(1L), List.of(sched1, sched2), true);
//        var result = raceRepository.getRace(roleName, id).orElse(null);
//        assertEquals(race, result);
//    }
//
//    @Test
//    void getRace_positive_notFound() {
//        assertNull(raceRepository.getRace(roleName, new RaceId(3)).orElse(null));
//    }
//
//    @Test
//    void getRaces_positive_got() {
//        var filter = new Filter(null, null, null, null, 1, null, Timestamp.valueOf("2025-04-01 10:00:00"),
//                Timestamp.valueOf("2025-04-01 11:59:59"));
//        var sched1 = new Schedule(new ScheduleId(1L), "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0);
//        var sched2 = new Schedule(new ScheduleId(2L), "second", Timestamp.valueOf("2025-04-01 11:40:00"), null, 5);
//        var race = new Race(new RaceId(1L), new TrainId(1L), List.of(sched1, sched2), true);
//        var result = raceRepository.getRaces(roleName, filter);
//        assertNotNull(result);
//        var iterator = result.iterator();
//        assertTrue(iterator.hasNext());
//        assertEquals(race, iterator.next());
//        assertFalse(iterator.hasNext());
//    }
//
//    @Test
//    void getRaces_positive_empty() {
//        var filter = new Filter(null, null, null, null, 1, null, Timestamp.valueOf("2025-04-01 00:00:00"),
//                Timestamp.valueOf("2025-04-01 11:11:11"));
//        var result = raceRepository.getRaces(roleName, filter);
//        assertNotNull(result);
//        assertFalse(result.iterator().hasNext());
//    }
//
//    @Test
//    void updateRace_positive_updates() {
//        var sched1 = new Schedule(new ScheduleId(1L), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0);
//        var sched2 = new Schedule(new ScheduleId(2L), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5);
//        var race = new Race(new RaceId(2L), new TrainId(2L), List.of(sched1, sched2), true);
//        raceRepository.updateRace(roleName, race.id(), race.finished());
//        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM races WHERE id = 2;"
//            )) {
//                try (var resultSet = statement.executeQuery()) {
//                    assertTrue(resultSet.next());
//                    assertEquals(race.trainId().id(), resultSet.getLong("train_id"));
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
//}