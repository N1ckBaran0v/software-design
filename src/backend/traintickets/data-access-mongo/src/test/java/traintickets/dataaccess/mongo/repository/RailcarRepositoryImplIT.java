package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.dataaccess.mongo.model.PlaceDocument;
import traintickets.dataaccess.mongo.model.RailcarDocument;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class RailcarRepositoryImplIT extends MongoIT {
    private RailcarRepository railcarRepository;
    private MongoCollection<RailcarDocument> railcarCollection;
    private MongoCollection<PlaceDocument> placeCollection;

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
        mongoExecutor.executeConsumer(session -> {
            var map = placeCollection.insertMany(session, List.of(
                    new PlaceDocument(null, 1, "", "universal", BigDecimal.valueOf(100)),
                    new PlaceDocument(null, 2, "", "child", BigDecimal.valueOf(50)),
                    new PlaceDocument(null, 1, "", "universal", BigDecimal.valueOf(100))
            )).getInsertedIds();
            var ids = List.of(
                    map.get(0).asObjectId().getValue(),
                    map.get(1).asObjectId().getValue(),
                    map.get(2).asObjectId().getValue()
            );
            railcarCollection.insertMany(session, List.of(
                    new RailcarDocument(null, "1", "сидячий", List.of(ids.get(0), ids.get(1))),
                    new RailcarDocument(null, "2", "купе", List.of(ids.get(2)))
            ));
        });
    }

//    @Override
//    protected void insertData() {
//        jdbcTemplate.executeCons(Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "insert into trains (train_class) values ('Скорый'); " +
//                            "insert into railcars (railcar_model, railcar_type) values " +
//                            "('1', 'сидячий'), ('2', 'купе'); " +
//                            "insert into railcars_in_trains (train_id, railcar_id) values " +
//                            "(1, 1), (1, 1), (1, 1), (1, 2); " +
//                            "insert into places (railcar_id, place_number, description, purpose, place_cost) values " +
//                            "(1, 1, '', 'universal', 100), (1, 2, '', 'child', 50), (2, 1, '', 'universal', 100);"
//            )) {
//                statement.execute();
//            }
//        });
//    }

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
        var id = mongoExecutor.executeFunction(session -> Objects.requireNonNull(railcarCollection.find(session,
                Filters.eq("model", "2")).first()).id().toHexString());
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
//
//    @Test
//    void getRailcarsByTrain() {
//    }
}