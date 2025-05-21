package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.FilterRepository;
import traintickets.dataaccess.mongo.connection.MongoExecutor;
import traintickets.dataaccess.mongo.model.FilterDocument;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

public final class FilterRepositoryImpl implements FilterRepository {
    private final MongoExecutor mongoExecutor;
    private final MongoCollection<FilterDocument> filterCollection;

    public FilterRepositoryImpl(MongoExecutor mongoExecutor) {
        this.mongoExecutor = mongoExecutor;
        filterCollection = mongoExecutor.getDatabase().getCollection("filters", FilterDocument.class);
    }

    @Override
    public void addFilter(Filter filter) {
        mongoExecutor.transactionConsumer(session -> {
            if (filterCollection.find(session, Filters.and(
                    Filters.eq("user", new ObjectId(filter.user().id())),
                    Filters.eq("name", filter.name())
            )).first() != null) {
                throw new EntityAlreadyExistsException(String.format("Filter %s already exists", filter));
            }
            filterCollection.insertOne(session, new FilterDocument(filter));
        });
    }

    @Override
    public Optional<Filter> getFilter(UserId userId, String name) {
        return mongoExecutor.executeFunction(session -> {
            var filter = Objects.requireNonNull(filterCollection.find(session, Filters.and(
                    Filters.eq("user", new ObjectId(userId.id())), Filters.eq("name", name)))).first();
            return Optional.ofNullable(filter == null ? null : filter.toFilter());
        });
    }

    @Override
    public Iterable<Filter> getFilters(UserId userId) {
        return mongoExecutor.executeFunction(session -> StreamSupport.stream(filterCollection.find(session,
                        Filters.eq("user", new ObjectId(userId.id()))).spliterator(), false)
                .map(FilterDocument::toFilter).toList());
    }

    @Override
    public void deleteFilter(UserId userId, String name) {
        mongoExecutor.executeConsumer(session -> filterCollection.deleteOne(session,
                Filters.and(Filters.eq("user", new ObjectId(userId.id())), Filters.eq("name", name))));
    }
}
