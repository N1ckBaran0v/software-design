package traintickets.dataaccess.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.businesslogic.transport.TransportUser;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplIT extends PostgresIT {
    private UserRepository userRepository;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        userRepository = new UserRepositoryImpl(jdbcTemplate);
    }

    @Override
    protected void insertData() {
        jdbcTemplate.executeCons(superuser, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "insert into users_view (user_name, pass_word, real_name, user_role, is_active) values " +
                            "('first', 'qwerty123', 'Иванов Иван Иванович', 'userRole', TRUE), " +
                            "('second', 'qwerty123', 'Петров Пётр Петрович', 'userRole', FALSE); "
            )) {
                statement.execute();
            }
        });
    }

    @Test
    void addUser_positive_added() {
        var user = new User(null, "random_username", "qwerty123", "Зубенко Михаил Петрович", "userRole", true);
        var result = userRepository.addUser(systemRole, user);
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(user.username(), result.username());
        assertEquals(user.password(), result.password());
        assertEquals(user.name(), result.name());
        assertEquals(user.role(), result.role());
        assertEquals(user.active(), result.active());
        jdbcTemplate.executeCons(superuser, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "select * from users_view where user_name = 'random_username';"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(user.username(), resultSet.getString("user_name"));
                    assertEquals(user.password(), resultSet.getString("pass_word"));
                    assertEquals(user.name(), resultSet.getString("real_name"));
                    assertEquals(user.role(), resultSet.getString("user_role"));
                    assertTrue(resultSet.getBoolean("is_active"));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void addUser_negative_exists() {
        var user = new User(null, "first", "qwerty123", "Зубенко Михаил Петрович", "userRole", true);
        assertThrows(EntityAlreadyExistsException.class, () -> userRepository.addUser(systemRole, user));
        jdbcTemplate.executeCons(superuser, Connection.TRANSACTION_READ_UNCOMMITTED, conn -> {
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
    void getUserById_positive_found() {
        var user = new User(new UserId("1"), "first", "qwerty123", "Иванов Иван Иванович", "userRole", true);
        assertEquals(user, userRepository.getUserById(systemRole, user.id()).orElse(null));
    }

    @Test
    void getUserById_positive_notFound() {
        assertTrue(userRepository.getUserById(systemRole, new UserId("3")).isEmpty());
    }

    @Test
    void getUserByUsername_positive_found() {
        var user = new User(new UserId("1"), "first", "qwerty123", "Иванов Иван Иванович", "userRole", true);
        assertEquals(user, userRepository.getUserByUsername(systemRole, user.username()).orElse(null));
    }

    @Test
    void getUserByUsername_positive_notFound() {
        assertTrue(userRepository.getUserByUsername(systemRole, "third").isEmpty());
    }

    @Test
    void getUsers_positive_allFoundByUsername() {
        var userId1 = new UserId("1");
        var user1 = new User(userId1, "first", "qwerty123", "Иванов Иван Иванович", "userRole", true);
        var userId2 = new UserId("2");
        var user2 = new User(userId2, "second", "qwerty123", "Петров Пётр Петрович", "userRole", false);
        var result = userRepository.getUsers(systemRole, List.of(userId1, userId2));
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(user1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(user2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getUsers_positive_someFoundByUsername() {
        var userId1 = new UserId("1");
        var user1 = new User(userId1, "first", "qwerty123", "Иванов Иван Иванович", "userRole", true);
        var result = userRepository.getUsers(systemRole, List.of(userId1, new UserId("3")));
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(user1, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getUsers_positive_noFoundByUsername() {
        var result = userRepository.getUsers(systemRole, List.of(new UserId("3"), new UserId("3")));
        assertNotNull(result);
        var iterator = result.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    void updateUserCompletely_positive_updated() {
        var user = new User(new UserId("1"), "third", "qwerty124", "Сидорович Иван Иванович", "unknownRole", false);
        userRepository.updateUserCompletely(systemRole, user);
        jdbcTemplate.executeCons(superuser, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "select * from users_view where user_name = 'third';"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(user.username(), resultSet.getString("user_name"));
                    assertEquals(user.password(), resultSet.getString("pass_word"));
                    assertEquals(user.name(), resultSet.getString("real_name"));
                    assertEquals(user.role(), resultSet.getString("user_role"));
                    assertFalse(resultSet.getBoolean("is_active"));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void updateUserCompletely_negative_exists() {
        var user = new User(new UserId("1"), "second", "qwerty124", "Сидорович Иван Иванович", "unknownRole", false);
        assertThrows(EntityAlreadyExistsException.class, () -> userRepository.updateUserCompletely(systemRole, user));
        jdbcTemplate.executeCons(superuser, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "select * from users_view where id = 1;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals("first", resultSet.getString("user_name"));
                    assertEquals("qwerty123", resultSet.getString("pass_word"));
                    assertEquals("Иванов Иван Иванович", resultSet.getString("real_name"));
                    assertEquals("userRole", resultSet.getString("user_role"));
                    assertTrue(resultSet.getBoolean("is_active"));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void updateUserPartially_positive_updated() {
        var user = new TransportUser(new UserId("1"), "third", "qwerty124", "Сидорович Иван Иванович");
        userRepository.updateUserPartially(systemRole, user);
        jdbcTemplate.executeCons(superuser, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "select * from users_view where user_name = 'third';"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(user.username(), resultSet.getString("user_name"));
                    assertEquals(user.password(), resultSet.getString("pass_word"));
                    assertEquals(user.name(), resultSet.getString("real_name"));
                    assertEquals("userRole", resultSet.getString("user_role"));
                    assertTrue(resultSet.getBoolean("is_active"));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void updateUserPartially_negative_exists() {
        var user = new TransportUser(new UserId("1"), "second", "qwerty124", "Сидорович Иван Иванович");
        assertThrows(EntityAlreadyExistsException.class, () -> userRepository.updateUserPartially(systemRole, user));
        jdbcTemplate.executeCons(superuser, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "select * from users_view where id = 1;"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals("first", resultSet.getString("user_name"));
                    assertEquals("qwerty123", resultSet.getString("pass_word"));
                    assertEquals("Иванов Иван Иванович", resultSet.getString("real_name"));
                    assertEquals("userRole", resultSet.getString("user_role"));
                    assertTrue(resultSet.getBoolean("is_active"));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void deleteUser_positive_banned() {
        var id = new UserId("1");
        userRepository.deleteUser(systemRole, id);
        jdbcTemplate.executeCons(superuser, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "select * from users_view where user_name = 'first';"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertFalse(resultSet.getBoolean("is_active"));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void deleteUser_positive_notFound() {
        var id = new UserId("3");
        userRepository.deleteUser(systemRole, id);
    }

    @Test
    void deleteUser_positive_alreadyBanned() {
        var id = new UserId("2");
        userRepository.deleteUser(systemRole, id);
        jdbcTemplate.executeCons(superuser, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "select * from users_view where user_name = 'second';"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertFalse(resultSet.getBoolean("is_active"));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void deleteUser_negative_denied() {
        var id = new UserId("1");
        assertThrows(RuntimeException.class, () -> userRepository.deleteUser(adminRole, id));
        jdbcTemplate.executeCons(superuser, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "select * from users_view where user_name = 'first';"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertTrue(resultSet.getBoolean("is_active"));
                    assertFalse(resultSet.next());
                }
            }
        });
    }
}