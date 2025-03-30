package traintickets.dataaccess.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.UserRepository;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplIT extends PostgresIT {
    private UserRepository userRepository;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        userRepository = new UserRepositoryImpl(jdbcTemplate, roleName);
    }

    @Override
    protected void insertData() {
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "insert into users_view (user_name, pass_word, real_name, user_role, is_active) values " +
                            "('first', 'qwerty123', 'Иванов Иван Иванович', 'userRole', TRUE), " +
                            "('second', 'qwerty123', 'Петров Пётр Петрович', 'userRole', TRUE); "
            )) {
                statement.execute();
            }
        });
    }

    @Test
    void addUser_positive_added() {
        var user = new User(null, "random_username", "qwerty123", "Зубенко Михаил Петрович", "userRole", true);
        userRepository.addUser(user);
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "select * from users_view where user_name = 'random_username';"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(user.username(), resultSet.getString(2));
                    assertEquals(user.password(), resultSet.getString(3));
                    assertEquals(user.name(), resultSet.getString(4));
                    assertEquals(user.role(), resultSet.getString(5));
                    assertTrue(resultSet.getBoolean(6));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void addUser_negative_exists() {
        var user = new User(null, "first", "qwerty123", "Зубенко Михаил Петрович", "userRole", true);
        assertThrows(EntityAlreadyExistsException.class, () -> userRepository.addUser(user));
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM users_view WHERE id = 3;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void getUser_positive_found() {
        var user = new User(new UserId(1), "first", "qwerty123", "Иванов Иван Иванович", "userRole", true);
        assertEquals(user, userRepository.getUser(user.username()).orElse(null));
    }

    @Test
    void getUser_positive_notFound() {
        assertTrue(userRepository.getUser("third").isEmpty());
    }

    @Test
    void getUsers_positive_allFound() {
        var userId1 = new UserId(1);
        var user1 = new User(userId1, "first", "qwerty123", "Иванов Иван Иванович", "userRole", true);
        var userId2 = new UserId(2);
        var user2 = new User(userId2, "second", "qwerty123", "Петров Пётр Петрович", "userRole", true);
        var result = userRepository.getUsers(List.of(userId1, userId2));
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(user1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(user2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getUsers_positive_someFound() {
        var userId1 = new UserId(1);
        var user1 = new User(userId1, "first", "qwerty123", "Иванов Иван Иванович", "userRole", true);
        var result = userRepository.getUsers(List.of(userId1, new UserId(3)));
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(user1, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getUsers_positive_noFound() {
        var result = userRepository.getUsers(List.of(new UserId(3), new UserId(4)));
        assertNotNull(result);
        var iterator = result.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    void updateUser_positive_updated() {
        var user = new User(new UserId(1), "third", "qwerty124", "Сидорович Иван Иванович", "unknownRole", false);
        userRepository.updateUser(user);
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "select * from users_view where user_name = 'third';"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(user.username(), resultSet.getString(2));
                    assertEquals(user.password(), resultSet.getString(3));
                    assertEquals(user.name(), resultSet.getString(4));
                    assertEquals(user.role(), resultSet.getString(5));
                    assertFalse(resultSet.getBoolean(6));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void updateUser_negative_exists() {
        var user = new User(new UserId(1), "second", "qwerty124", "Сидорович Иван Иванович", "unknownRole", false);
        assertThrows(EntityAlreadyExistsException.class, () -> userRepository.updateUser(user));
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "select * from users_view where id = 1;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals("first", resultSet.getString(2));
                    assertEquals("qwerty123", resultSet.getString(3));
                    assertEquals("Иванов Иван Иванович", resultSet.getString(4));
                    assertEquals("userRole", resultSet.getString(5));
                    assertTrue(resultSet.getBoolean(6));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void deleteUser_positive_banned() {
        var id = new UserId(1);
        userRepository.deleteUser(id);
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "select * from users_view where user_name = 'first';"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertFalse(resultSet.getBoolean(6));
                    assertFalse(resultSet.next());
                }
            }
        });
    }
}