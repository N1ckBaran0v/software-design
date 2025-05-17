package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.model.RailcarId;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.TrainRepository;
import traintickets.dataaccess.mongo.model.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TrainRepositoryImplIT extends MongoIT {
    private TrainRepository trainRepository;
    private MongoCollection<TrainDocument> trainCollection;
    private MongoCollection<RailcarDocument> railcarCollection;
    private MongoCollection<RaceDocument> raceCollection;
    private MongoCollection<ScheduleDocument> scheduleCollection;

    private ObjectId railcarId;
    private List<ObjectId> trainIds, raceIds;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        trainRepository = new TrainRepositoryImpl(mongoExecutor);
    }

    @AfterEach
    @Override
    void tearDown() {
        super.tearDown();
    }

    @Override
    protected void insertData() {
        trainCollection = mongoExecutor.getDatabase().getCollection("trains", TrainDocument.class);
        railcarCollection = mongoExecutor.getDatabase().getCollection("railcars", RailcarDocument.class);
        raceCollection = mongoExecutor.getDatabase().getCollection("races", RaceDocument.class);
        scheduleCollection = mongoExecutor.getDatabase().getCollection("schedules", ScheduleDocument.class);
        mongoExecutor.executeConsumer(session -> {
            railcarId = Objects.requireNonNull(railcarCollection.insertOne(session,
                    new RailcarDocument(null, "1", "сидячий")).getInsertedId()).asObjectId().getValue();
            var trainMap = trainCollection.insertMany(session, List.of(
                    new TrainDocument(null, "фирменный", List.of(railcarId)),
                    new TrainDocument(null, "скорый", List.of())
            )).getInsertedIds();
            trainIds = List.of(trainMap.get(0).asObjectId().getValue(), trainMap.get(1).asObjectId().getValue());
            var raceMap = raceCollection.insertMany(session, List.of(
                    new RaceDocument(null, trainIds.get(0), true),
                    new RaceDocument(null, trainIds.get(1), false)
            )).getInsertedIds();
            raceIds = List.of(raceMap.get(0).asObjectId().getValue(), raceMap.get(1).asObjectId().getValue());
            scheduleCollection.insertMany(session, List.of(
                    new ScheduleDocument(null, raceIds.get(0), "first", null, Timestamp.valueOf("2025-04-01 10:10:00"), 0),
                    new ScheduleDocument(null, raceIds.get(0), "second", Timestamp.valueOf("2025-04-01 11:40:00"), null, 5),
                    new ScheduleDocument(null, raceIds.get(1), "first", null, Timestamp.valueOf("2025-04-01 11:00:00"), 0),
                    new ScheduleDocument(null, raceIds.get(1), "second", Timestamp.valueOf("2025-04-01 12:00:00"), null, 5)
            ));
        });
    }

    @Test
    void addTrain_positive_added() {
        var train = new Train(null, "скорый", List.of(new RailcarId(railcarId.toHexString())));
        trainRepository.addTrain(train);
        mongoExecutor.executeConsumer(session -> {
            assertEquals(3, trainCollection.countDocuments());
            assertEquals(1, railcarCollection.countDocuments());
        });
    }

    @Test
    void getTrain_positive_found() {
        var id = trainIds.getFirst().toHexString();
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

    @Test
    void getTrains_positive_got() {
        var train = new Train(new TrainId(trainIds.getFirst().toHexString()), "фирменный",
                List.of(new RailcarId(railcarId.toHexString())));
        var result = trainRepository.getTrains(Timestamp.valueOf("2025-04-01 11:50:00"),
                Timestamp.valueOf("2025-04-01 13:20:00"));
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(train, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getTrains_positive_empty() {
        var result = trainRepository.getTrains(Timestamp.valueOf("2025-04-01 11:30:00"),
                Timestamp.valueOf("2025-04-01 12:30:00"));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }
}