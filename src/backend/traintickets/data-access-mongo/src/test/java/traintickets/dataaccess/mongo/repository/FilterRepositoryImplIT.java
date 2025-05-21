package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.FilterRepository;
import traintickets.dataaccess.mongo.model.FilterDocument;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class FilterRepositoryImplIT extends MongoIT {
    private FilterRepository filterRepository;
    private MongoCollection<FilterDocument> filterCollection;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        filterRepository = new FilterRepositoryImpl(mongoExecutor);
    }

    @AfterEach
    @Override
    void tearDown() {
        super.tearDown();
    }

    @Override
    protected void insertData() {
        filterCollection = mongoExecutor.getDatabase().getCollection("filters", FilterDocument.class);
        mongoExecutor.executeConsumer(session -> filterCollection.insertMany(session, List.of(
                new FilterDocument(null, new ObjectId("123456789012345678901234"), "first", "first", "second", 0,
                        Map.of("adult", 2, "child", 1)),
                new FilterDocument(null, new ObjectId("123456789012345678901234"), "second", "first", "second", 1,
                        Map.of("adult", 1))
        )));
    }

    @Test
    void addFilter_positive_added() {
        var filter = new Filter(new UserId("123456789012345678901234"), "third", "first", "second", 0,
                Map.of("adult", 1), null, null);
        filterRepository.addFilter(filter);
        mongoExecutor.executeConsumer(session -> assertEquals(3, filterCollection.countDocuments(session)));
    }

    @Test
    void addFilter_negative_exists() {
        var filter = new Filter(new UserId("123456789012345678901234"), "first", "first", "second", 0,
                Map.of("adult", 1), null, null);
        assertThrows(EntityAlreadyExistsException.class, () -> filterRepository.addFilter(filter));
        mongoExecutor.executeConsumer(session -> assertEquals(2, filterCollection.countDocuments(session)));
    }

    @Test
    void getFilter_positive_found() {
        var userId = new UserId("123456789012345678901234");
        var filter = new Filter(userId, "first", "first", "second", 0, Map.of("adult", 2, "child", 1), null, null);
        var result = filterRepository.getFilter(userId, filter.name()).orElse(null);
        assertEquals(filter, result);
    }

    @Test
    void getFilter_positive_notFound() {
        assertNull(filterRepository.getFilter(new UserId("123456789012345678901234"), "third").orElse(null));
    }

    @Test
    void getFilters_positive_got() {
        var userId = new UserId("123456789012345678901234");
        var filter1 = new Filter(userId, "first", "first", "second", 0, Map.of("adult", 2, "child", 1), null, null);
        var filter2 = new Filter(userId, "second", "first", "second", 1, Map.of("adult", 1), null, null);
        var result = filterRepository.getFilters(userId);
        assertNotNull(result);
        var list = StreamSupport.stream(result.spliterator(), false).toList();
        assertEquals(2, list.size());
        assertTrue(filter1.equals(list.get(0)) || filter1.equals(list.get(1)));
        assertTrue(filter2.equals(list.get(0)) || filter2.equals(list.get(1)));
    }

    @Test
    void getFilters_positive_empty() {
        var result = filterRepository.getFilters(new UserId("987654321098765432109876"));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void deleteFilter_positive_deleted() {
        var userId = new UserId("123456789012345678901234");
        var name = "first";
        filterRepository.deleteFilter(userId, name);
        mongoExecutor.executeConsumer(session -> assertNull(filterCollection.find(session, Filters.and(
                Filters.eq("user", new ObjectId(userId.id())), Filters.eq("name", name)
        )).first()));
    }
}