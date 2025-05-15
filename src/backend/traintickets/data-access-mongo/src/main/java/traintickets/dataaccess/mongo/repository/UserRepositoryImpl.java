package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.types.ObjectId;
import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.businesslogic.transport.TransportUser;
import traintickets.dataaccess.mongo.connection.MongoExecutor;
import traintickets.dataaccess.mongo.model.UserDocument;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

public final class UserRepositoryImpl implements UserRepository {
    private final MongoExecutor mongoExecutor;
    private final MongoCollection<UserDocument> userCollection;

    public UserRepositoryImpl(MongoExecutor mongoExecutor) {
        this.mongoExecutor = Objects.requireNonNull(mongoExecutor);
        userCollection = mongoExecutor.getDatabase().getCollection("users", UserDocument.class);
    }

    @Override
    public User addUser(User user) {
        return mongoExecutor.transactionFunction(session -> {
            var possible = userCollection.find(session, Filters.eq("username", user.username())).first();
            if (possible != null) {
                    throw new EntityAlreadyExistsException(
                            String.format("User with username '%s' already exists", user.username()));
            }
            var id = userCollection.insertOne(session, new UserDocument(user)).getInsertedId();
            return Objects.requireNonNull(userCollection.find(session, Filters.eq("_id", id)).first()).toUser();
        });
    }

    @Override
    public Optional<User> getUserById(UserId userId) {
        return mongoExecutor.executeFunction(session -> {
            var found = userCollection.find(session, Filters.eq("_id", new ObjectId(userId.id()))).first();
            return Optional.ofNullable(found == null ? null : found.toUser());
        });
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return mongoExecutor.executeFunction(session -> {
            var found = userCollection.find(session, Filters.eq("username", username)).first();
            return Optional.ofNullable(found == null ? null : found.toUser());
        });
    }

    @Override
    public Iterable<User> getUsers(Iterable<UserId> userIds) {
        return mongoExecutor.executeFunction(session -> StreamSupport.stream(userCollection.find(session,
                        Filters.in("_id", StreamSupport.stream(userIds.spliterator(), false)
                        .map(id -> new ObjectId(id.id())).toList())).spliterator(), false)
                .map(UserDocument::toUser).toList());
    }

    @Override
    public void updateUserCompletely(User user) {
        mongoExecutor.transactionConsumer(session -> {
            var found = userCollection.find(session, Filters.eq("username", user.username())).first();
            if (found != null && !user.id().id().equals(found.id().toHexString())) {
                throw new EntityAlreadyExistsException(
                        String.format("User with username '%s' already exists", user.username()));
            }
            userCollection.updateOne(Filters.eq("_id", new ObjectId(user.id().id())), Updates.combine(
                    Updates.set("username", user.username()),
                    Updates.set("password", user.password()),
                    Updates.set("name", user.name()),
                    Updates.set("role", user.role()),
                    Updates.set("active", user.active())
            ));
        });
    }

    @Override
    public void updateUserPartially(TransportUser user) {
        mongoExecutor.transactionConsumer(session -> {
            var found = userCollection.find(session, Filters.eq("username", user.username())).first();
            if (found != null && !user.id().id().equals(found.id().toHexString())) {
                throw new EntityAlreadyExistsException(
                        String.format("User with username '%s' already exists", user.username()));
            }
            userCollection.updateOne(Filters.eq("_id", new ObjectId(user.id().id())), Updates.combine(
                    Updates.set("username", user.username()),
                    Updates.set("password", user.password()),
                    Updates.set("name", user.name())
            ));
        });
    }

    @Override
    public void deleteUser(UserId userId) {
        mongoExecutor.executeConsumer(session ->
                userCollection.updateOne(Filters.eq("_id", new ObjectId(userId.id())), Updates.set("active", false)));
    }
}
