package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.businesslogic.transport.TransportUser;
import traintickets.dataaccess.mongo.model.UserDocument;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplIT extends MongoIT {
    private UserRepository userRepository;
    private MongoCollection<UserDocument> userCollection;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        userRepository = new UserRepositoryImpl(mongoExecutor);
    }

    @AfterEach
    @Override
    void tearDown() {
        super.tearDown();
    }

    @Override
    protected void insertData() {
        userCollection = mongoExecutor.getDatabase().getCollection("users", UserDocument.class);
        mongoExecutor.executeConsumer(session -> userCollection.insertMany(session, List.of(
                new UserDocument(null, "first", "qwerty123", "Иванов Иван Иванович", "user_role", true),
                new UserDocument(null, "second", "qwerty123", "Петров Пётр Петрович", "user_role", false)
        )));
    }

    @Test
    void addUser_positive_added() {
        var user = new User(null, "random_username", "qwerty123", "Zubenko Mikhail Petrovich", "user_role", true);
        var result = userRepository.addUser(user);
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(user.username(), result.username());
        assertEquals(user.password(), result.password());
        assertEquals(user.name(), result.name());
        assertEquals(user.role(), result.role());
        assertEquals(user.active(), result.active());
        mongoExecutor.executeConsumer(session -> {
            var found = userCollection.find(Filters.eq("username", result.username())).first();
            assertEquals(new UserDocument(result), found);
        });
    }

    @Test
    void addUser_negative_exists() {
        var user = new User(null, "first", "qwerty123", "Zubenko Mikhail Petrovich", "user_role", true);
        assertThrows(EntityAlreadyExistsException.class, () -> userRepository.addUser(user));
        mongoExecutor.executeConsumer(session ->
                assertEquals(2, StreamSupport.stream(userCollection.find().spliterator(), false).toList().size()));
    }

    @Test
    void getUserById_positive_found() {
        var id = mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "first"))
                        .first()).id().toHexString());
        var user = new User(new UserId(id), "first", "qwerty123", "Иванов Иван Иванович", "user_role", true);
        assertEquals(user, userRepository.getUserById(user.id()).orElse(null));
    }

    @Test
    void getUserById_positive_notFound() {
        var user = new User(new UserId("123456789012345678901234"),
                "first", "qwerty123", "Иванов Иван Иванович", "user_role", true);
        assertNull(userRepository.getUserById(user.id()).orElse(null));
    }

    @Test
    void getUserByUsername_positive_found() {
        var id = mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "first"))
                        .first()).id().toHexString());
        var user = new User(new UserId(id), "first", "qwerty123", "Иванов Иван Иванович", "user_role", true);
        assertEquals(user, userRepository.getUserByUsername(user.username()).orElse(null));
    }

    @Test
    void getUserByUsername_positive_notFound() {
        assertNull(userRepository.getUserByUsername("nekto").orElse(null));
    }

    @Test
    void getUsers_positive_allFoundByUsername() {
        var userId1 = new UserId(mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "first"))
                        .first()).id().toHexString()));
        var user1 = new User(userId1, "first", "qwerty123", "Иванов Иван Иванович", "user_role", true);
        var userId2 = new UserId(mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "second"))
                        .first()).id().toHexString()));
        var user2 = new User(userId2, "second", "qwerty123", "Петров Пётр Петрович", "user_role", false);
        var result = userRepository.getUsers(List.of(userId1, userId2));
        assertNotNull(result);
        var list = StreamSupport.stream(result.spliterator(), false).toList();
        assertEquals(2, list.size());
        assertTrue(user1.equals(list.get(0)) || user1.equals(list.get(1)));
        assertTrue(user2.equals(list.get(0)) || user2.equals(list.get(1)));
    }

    @Test
    void getUsers_positive_someFoundByUsername() {
        var userId1 = new UserId(mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "first"))
                        .first()).id().toHexString()));
        var user1 = new User(userId1, "first", "qwerty123", "Иванов Иван Иванович", "user_role", true);
        var result = userRepository.getUsers(List.of(userId1, userId1, new UserId("123456789012345678901234")));
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(user1, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getUsers_positive_noFoundByUsername() {
        var result = userRepository.getUsers(List.of(new UserId("123456789012345678901234")));
        assertNotNull(result);
        var iterator = result.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    void updateUserCompletely_positive_updated1() {
        var userId = new UserId(mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "first"))
                        .first()).id().toHexString()));
        var user = new User(userId, "third", "qwerty124", "Сидорович Иван Иванович", "unknownRole", false);
        userRepository.updateUserCompletely(user);
        mongoExecutor.executeConsumer(session -> {
            var found = userCollection.find(Filters.eq("username", user.username())).first();
            assertEquals(new UserDocument(user), found);
        });
    }

    @Test
    void updateUserCompletely_positive_updated2() {
        var userId = new UserId(mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "first"))
                        .first()).id().toHexString()));
        var user = new User(userId, "first", "qwerty124", "Сидорович Иван Иванович", "unknownRole", false);
        userRepository.updateUserCompletely(user);
        mongoExecutor.executeConsumer(session -> {
            var found = userCollection.find(Filters.eq("username", user.username())).first();
            assertEquals(new UserDocument(user), found);
        });
    }

    @Test
    void updateUserCompletely_negative_exists() {
        var userId = new UserId(mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "first"))
                        .first()).id().toHexString()));
        var user = new User(userId, "second", "qwerty124", "Сидорович Иван Иванович", "unknownRole", false);
        assertThrows(EntityAlreadyExistsException.class, () -> userRepository.updateUserCompletely(user));
        mongoExecutor.executeConsumer(session -> {
            var found = userCollection.find(Filters.eq("username", user.username())).first();
            assertNotEquals(new UserDocument(user), found);
        });
    }

    @Test
    void updateUserPartially_positive_updated1() {
        var userId = new UserId(mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "first"))
                        .first()).id().toHexString()));
        var user = new User(userId, "third", "qwerty124", "Сидорович Иван Иванович", "user_role", true);
        userRepository.updateUserPartially(TransportUser.from(user));
        mongoExecutor.executeConsumer(session -> {
            var found = userCollection.find(Filters.eq("username", user.username())).first();
            assertEquals(new UserDocument(user), found);
        });
    }

    @Test
    void updateUserPartially_positive_updated2() {
        var userId = new UserId(mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "first"))
                        .first()).id().toHexString()));
        var user = new User(userId, "first", "qwerty124", "Сидорович Иван Иванович", "user_role", true);
        userRepository.updateUserPartially(TransportUser.from(user));
        mongoExecutor.executeConsumer(session -> {
            var found = userCollection.find(Filters.eq("username", user.username())).first();
            assertEquals(new UserDocument(user), found);
        });
    }

    @Test
    void updateUserPartially_negative_exists() {
        var userId = new UserId(mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "first"))
                        .first()).id().toHexString()));
        var user = new User(userId, "second", "qwerty124", "Сидорович Иван Иванович", "user_role", true);
        assertThrows(EntityAlreadyExistsException.class, () -> userRepository.updateUserPartially(TransportUser.from(user)));
        mongoExecutor.executeConsumer(session -> {
            var found = userCollection.find(Filters.eq("username", user.username())).first();
            assertNotEquals(new UserDocument(user), found);
        });
    }

    @Test
    void deleteUser_positive_banned() {
        var userId = new UserId(mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "first"))
                        .first()).id().toHexString()));
        userRepository.deleteUser(userId);
        mongoExecutor.executeConsumer(session -> {
            var user = new User(userId, "first", "qwerty123", "Иванов Иван Иванович", "user_role", false);
            var found = userCollection.find(Filters.eq("username", user.username())).first();
            assertEquals(new UserDocument(user), found);
        });
    }

    @Test
    void deleteUser_positive_notFound() {
        var id = new UserId("123456789012345678901234");
        userRepository.deleteUser(id);
    }

    @Test
    void deleteUser_positive_alreadyBanned() {
        var userId = new UserId(mongoExecutor.executeFunction(session ->
                Objects.requireNonNull(userCollection.find(Filters.eq("username", "second"))
                        .first()).id().toHexString()));
        userRepository.deleteUser(userId);
        mongoExecutor.executeConsumer(session -> {
            var user = new User(userId, "second", "qwerty123", "Петров Пётр Петрович", "user_role", false);
            var found = userCollection.find(Filters.eq("username", user.username())).first();
            assertEquals(new UserDocument(user), found);
        });
    }
}