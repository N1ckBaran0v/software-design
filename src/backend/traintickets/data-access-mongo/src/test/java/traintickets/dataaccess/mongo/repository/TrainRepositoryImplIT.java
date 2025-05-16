package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.model.RailcarId;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.TrainRepository;
import traintickets.dataaccess.mongo.model.PlaceDocument;
import traintickets.dataaccess.mongo.model.RailcarDocument;
import traintickets.dataaccess.mongo.model.TrainDocument;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TrainRepositoryImplIT extends MongoIT {
    private TrainRepository trainRepository;
    private MongoCollection<TrainDocument> trainCollection;
    private MongoCollection<RailcarDocument> railcarCollection;

    @BeforeEach
    void setUp() {
        super.setUp();
        trainRepository = new TrainRepositoryImpl(mongoExecutor);
    }

    @AfterEach
    void tearDown() {
        super.tearDown();
    }

    @Override
    protected void insertData() {
        trainCollection = mongoExecutor.getDatabase().getCollection("trains", TrainDocument.class);
        railcarCollection = mongoExecutor.getDatabase().getCollection("railcars", RailcarDocument.class);
        mongoExecutor.executeConsumer(session -> {
            var railcarId = Objects.requireNonNull(railcarCollection.insertOne(session,
                    new RailcarDocument(null, "1", "сидячий", List.of())).getInsertedId()).asObjectId().getValue();
            trainCollection.insertMany(session, List.of(
                    new TrainDocument(null, "фирменный", List.of(railcarId)),
                    new TrainDocument(null, "скорый", List.of())
            ));
        });
    }

//    @Override
//    protected void insertData() {
//        jdbcTemplate.executeCons(Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
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
//                            "insert into railcars_in_trains (train_id, railcar_id) values (1, 1); "
//            )) {
//                statement.execute();
//            }
//        });
//    }

    @Test
    void addTrain_positive_added() {
        var railcarId = mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(railcarCollection.find(session).first()).id().toHexString());
        var train = new Train(null, "скорый", List.of(new RailcarId(railcarId)));
        trainRepository.addTrain(train);
        mongoExecutor.executeConsumer(session -> {
            assertEquals(3, trainCollection.countDocuments());
            assertEquals(1, railcarCollection.countDocuments());
        });
    }

    @Test
    void getTrain_positive_found() {
        var id = mongoExecutor.executeFunction(session -> Objects.requireNonNull(
                trainCollection.find(session, Filters.eq("trainClass", "фирменный")).first()).id().toHexString());
        var result = trainRepository.getTrain(new TrainId(id)).orElse(null);
        assertNotNull(result);
        assertEquals("фирменный", result.trainClass());
        assertEquals(1, result.railcars().size());
    }

    @Test
    void getTrain_positive_notFound() {
        var result = trainRepository.getTrain(new TrainId("123456789012345678901234")).orElse(null);
        assertNull(result);
    }

//    @Test
//    void getTrains_positive_got() {
//        var train = new Train(new TrainId("1"), "фирменный", List.of());
//        var result = trainRepository.getTrains(Timestamp.valueOf("2025-04-01 11:50:00"),
//                Timestamp.valueOf("2025-04-01 13:20:00"));
//        assertNotNull(result);
//        var iterator = result.iterator();
//        assertTrue(iterator.hasNext());
//        assertEquals(train, iterator.next());
//        assertFalse(iterator.hasNext());
//    }
//
//    @Test
//    void getTrains_positive_empty() {
//        var result = trainRepository.getTrains(Timestamp.valueOf("2025-04-01 11:30:00"),
//                Timestamp.valueOf("2025-04-01 12:30:00"));
//        assertNotNull(result);
//        assertFalse(result.iterator().hasNext());
//    }
}