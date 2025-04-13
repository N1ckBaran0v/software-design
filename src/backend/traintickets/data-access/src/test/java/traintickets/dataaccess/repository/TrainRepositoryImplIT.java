package traintickets.dataaccess.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.model.RailcarId;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.TrainRepository;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainRepositoryImplIT extends PostgresIT {
    private TrainRepository trainRepository;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        trainRepository = new TrainRepositoryImpl(jdbcTemplate);
    }

    @Override
    protected void insertData() {
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "insert into trains (train_class) values ('фирменный'), ('Скорый'); " +
                            "insert into races (train_id, finished) values " +
                            "(1, true), (2, false); " +
                            "insert into schedule (race_id, station_name, arrival, departure, multiplier) values " +
                            "(1, 'first', null, '2025-04-01 10:10:00+03', 0), " +
                            "(1, 'second', '2025-04-01 11:40:00+03', null, 5), " +
                            "(2, 'first', null, '2025-04-01 11:00:00+03', 0), " +
                            "(2, 'second', '2025-04-01 12:00:00+03', null, 5); " +
                            "insert into railcars (railcar_model, railcar_type) values (1, 'сидячий'); " +
                            "insert into railcars_in_trains (train_id, railcar_id) values (1, 1); "
            )) {
                statement.execute();
            }
        });
    }

    @Test
    void addTrain_positive_added() {
        var train = new Train(null, "Скорый", List.of(new RailcarId("1")));
        trainRepository.addTrain(roleName, train);
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM trains WHERE id = 3;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(train.trainClass(), resultSet.getString("train_class"));
                    assertFalse(resultSet.next());
                }
            }
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM railcars_in_trains WHERE train_id = 3;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(train.railcars().getFirst().id(), String.valueOf(resultSet.getLong("railcar_id")));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void getTrain_positive_found() {
        var id = new TrainId("1");
        var train = new Train(id, "фирменный", List.of(new RailcarId("1")));
        var result = trainRepository.getTrain(roleName, id).orElse(null);
        assertNotNull(result);
        assertEquals(train, result);
    }

    @Test
    void getTrain_positive_notFound() {
        var result = trainRepository.getTrain(roleName, new TrainId("3")).orElse(null);
        assertNull(result);
    }

    @Test
    void getTrains_positive_got() {
        var train = new Train(new TrainId("1"), "фирменный", List.of());
        var result = trainRepository.getTrains(roleName, Timestamp.valueOf("2025-04-01 11:50:00"),
                Timestamp.valueOf("2025-04-01 13:20:00"));
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(train, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getTrains_positive_empty() {
        var result = trainRepository.getTrains(roleName, Timestamp.valueOf("2025-04-01 11:30:00"),
                Timestamp.valueOf("2025-04-01 12:30:00"));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }
}