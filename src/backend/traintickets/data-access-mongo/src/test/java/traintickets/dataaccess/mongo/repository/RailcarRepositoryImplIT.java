package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.dataaccess.mongo.model.PlaceDocument;
import traintickets.dataaccess.mongo.model.RailcarDocument;
import traintickets.dataaccess.mongo.model.TrainDocument;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class RailcarRepositoryImplIT extends MongoIT {
    private RailcarRepository railcarRepository;
    private MongoCollection<RailcarDocument> railcarCollection;
    private MongoCollection<PlaceDocument> placeCollection;
    private MongoCollection<TrainDocument> trainCollection;
    private List<ObjectId> placeIds;
    private ObjectId first, second, train;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        railcarRepository = new RailcarRepositoryImpl(mongoExecutor);
    }

    @AfterEach
    @Override
    void tearDown() {
        super.tearDown();
    }

    @Override
    protected void insertData() {
        railcarCollection = mongoExecutor.getDatabase().getCollection("railcars", RailcarDocument.class);
        placeCollection = mongoExecutor.getDatabase().getCollection("places", PlaceDocument.class);
        trainCollection = mongoExecutor.getDatabase().getCollection("trains", TrainDocument.class);
        mongoExecutor.executeConsumer(session -> {
            var map = placeCollection.insertMany(session, List.of(
                    new PlaceDocument(null, 1, "", "universal", BigDecimal.valueOf(100)),
                    new PlaceDocument(null, 2, "", "child", BigDecimal.valueOf(50)),
                    new PlaceDocument(null, 1, "", "universal", BigDecimal.valueOf(100))
            )).getInsertedIds();
            placeIds = List.of(
                    map.get(0).asObjectId().getValue(),
                    map.get(1).asObjectId().getValue(),
                    map.get(2).asObjectId().getValue()
            );
            var railcarMap = railcarCollection.insertMany(session, List.of(
                    new RailcarDocument(null, "1", "сидячий", List.of(placeIds.get(0), placeIds.get(1))),
                    new RailcarDocument(null, "2", "купе", List.of(placeIds.get(2)))
            )).getInsertedIds();
            first = railcarMap.get(0).asObjectId().getValue();
            second = railcarMap.get(1).asObjectId().getValue();
            train = Objects.requireNonNull(trainCollection.insertOne(session, new TrainDocument(null, "Скорый",
                    List.of(first, first, first, second))).getInsertedId()).asObjectId().getValue();
        });
    }

    @Test
    void addRailcar_positive_added() {
        var place = new Place(null, 1, "", "universal", BigDecimal.valueOf(100));
        var railcar = new Railcar(null, "3", "купе", List.of(place));
        railcarRepository.addRailcar(railcar);
        mongoExecutor.executeConsumer(session -> {
            var foundRailcar = railcarCollection.find(session, Filters.eq("model", "3")).first();
            assertNotNull(foundRailcar);
            assertEquals(railcar.type(), foundRailcar.type());
            var places = StreamSupport.stream(placeCollection.find(session,
                    Filters.in("_id", foundRailcar.places())).spliterator(), false).toList();
            assertEquals(1, places.size());
            var foundPlace = places.getFirst();
            assertEquals(place.number(), foundPlace.number());
            assertEquals(place.description(), foundPlace.description());
            assertEquals(place.purpose(), foundPlace.purpose());
            assertEquals(place.cost(), foundPlace.cost());
        });
    }

    @Test
    void addRailcar_negative_exists() {
        var place = new Place(null, 1, "", "universal", BigDecimal.valueOf(100));
        var railcar = new Railcar(null, "1", "купе", List.of(place));
        assertThrows(EntityAlreadyExistsException.class, () -> railcarRepository.addRailcar(railcar));
        mongoExecutor.executeConsumer(session -> {
            assertEquals(2, railcarCollection.countDocuments());
            assertEquals(3, placeCollection.countDocuments());
        });
    }

    @Test
    void getRailcarsByType_positive_got() {
        var id = second.toHexString();
        var result = railcarRepository.getRailcarsByType("купе");
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(id, iterator.next().id().id());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getRailcarsByType_positive_empty() {
        var result = railcarRepository.getRailcarsByType("unexpexted");
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void getRailcarsByTrain_positive_got() {
        var placeId1 = new PlaceId(placeIds.get(0).toHexString());
        var placeId2 = new PlaceId(placeIds.get(1).toHexString());
        var placeId3 = new PlaceId(placeIds.get(2).toHexString());
        var place1 = new Place(placeId1, 1, "", "universal", BigDecimal.valueOf(100));
        var place2 = new Place(placeId2, 2, "", "child", BigDecimal.valueOf(50));
        var place3 = new Place(placeId3, 1, "", "universal", BigDecimal.valueOf(100));
        var railcarId1 = new RailcarId(first.toHexString());
        var railcarId2 = new RailcarId(second.toHexString());
        var railcar1 = new Railcar(railcarId1, "1", "сидячий", List.of(place1, place2));
        var railcar2 = new Railcar(railcarId2, "2", "купе", List.of(place3));
        var trainId = new TrainId(train.toHexString());
        var result = railcarRepository.getRailcarsByTrain(trainId);
        assertNotNull(result);
        var list = StreamSupport.stream(result.spliterator(), false).toList();
        assertEquals(2, list.size());
        assertTrue(railcar1.equals(list.get(0)) || railcar1.equals(list.get(1)));
        assertTrue(railcar2.equals(list.get(0)) || railcar2.equals(list.get(1)));
    }

    @Test
    void getRailcarsByTrain_positive_empty() {
        var result = railcarRepository.getRailcarsByTrain(new TrainId("123456789012345678901234"));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

//    @Test
//    void getRailcarsByTrain_positive_got() {
//        var place1 = new Place(new PlaceId("1"), 1, "", "universal", BigDecimal.valueOf(100));
//        var place2 = new Place(new PlaceId("2"), 2, "", "child", BigDecimal.valueOf(50));
//        var place3 = new Place(new PlaceId("3"), 1, "", "universal", BigDecimal.valueOf(100));
//        var railcar1 = new Railcar(new RailcarId("1"), "1", "сидячий", List.of(place1, place2));
//        var railcar2 = new Railcar(new RailcarId("2"), "2", "купе", List.of(place3));
//        var result = railcarRepository.getRailcarsByTrain(new TrainId("1"));
//        assertNotNull(result);
//        var iterator = result.iterator();
//        assertTrue(iterator.hasNext());
//        assertEquals(railcar1, iterator.next());
//        assertTrue(iterator.hasNext());
//        assertEquals(railcar2, iterator.next());
//        assertFalse(iterator.hasNext());
//    }
//
//    @Test
//    void getRailcarsByTrain_positive_empty() {
//        var result = railcarRepository.getRailcarsByTrain(new TrainId("2"));
//        assertNotNull(result);
//        assertFalse(result.iterator().hasNext());
//    }
}