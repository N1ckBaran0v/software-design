package traintickets.dataaccess.postgres.repository;

import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.businesslogic.transport.TransportUser;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

public final class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
    }

    @Override
    public User addUser(User user) {
        return jdbcTemplate.executeFunc(Connection.TRANSACTION_SERIALIZABLE, connection -> {
            checkIfExists(user.id(), user.username(), connection);
            try (var statement = connection.prepareStatement(
                    "INSERT INTO users_view (user_name, pass_word, real_name, user_role, is_active)\n" +
                            "VALUES (?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, user.username());
                statement.setString(2, user.password());
                statement.setString(3, user.name());
                statement.setString(4, user.role());
                statement.setBoolean(5, user.active());
                statement.executeUpdate();
                var rs = statement.getGeneratedKeys();
                rs.next();
                var userId = new UserId(String.valueOf(rs.getLong(1)));
                return new User(userId, user.username(), user.password(), user.name(), user.role(), user.active());
            }
        });
    }

    @Override
    public Optional<User> getUserById(UserId userId) {
        return jdbcTemplate.executeFunc(Connection.TRANSACTION_READ_COMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM users_view WHERE id = (?);"
            )) {
                statement.setLong(1, Long.parseLong(userId.id()));
                try (var resultSet = statement.executeQuery()) {
                    return Optional.ofNullable(getUser(resultSet));
                }
            }
        });
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return jdbcTemplate.executeFunc(Connection.TRANSACTION_READ_COMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM users_view WHERE user_name = (?);"
            )) {
                statement.setString(1, username);
                try (var resultSet = statement.executeQuery()) {
                    return Optional.ofNullable(getUser(resultSet));
                }
            }
        });
    }

    @Override
    public Iterable<User> getUsers(Iterable<UserId> userIds) {
        return jdbcTemplate.executeFunc(Connection.TRANSACTION_READ_COMMITTED, connection -> {
            var ids = StreamSupport.stream(userIds.spliterator(), false).map(id -> String.valueOf(id.id())).toList();
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM users_view WHERE id IN ('" + String.join("', '", ids) + "');"
            )) {
                try (var resultSet = statement.executeQuery()) {
                    var result = new ArrayList<User>();
                    var user = getUser(resultSet);
                    while (user != null) {
                        result.add(user);
                        user = getUser(resultSet);
                    }
                    return result;
                }
            }
        });
    }

    @Override
    public void updateUserCompletely(User user) {
        jdbcTemplate.executeCons(Connection.TRANSACTION_SERIALIZABLE, connection -> {
            checkIfExists(user.id(), user.username(), connection);
            try (var statement = connection.prepareStatement(
                    "UPDATE users_view SET " +
                            "user_name = (?), " +
                            "pass_word = (?), " +
                            "real_name = (?), " +
                            "user_role = (?), " +
                            "is_active = (?) " +
                            "WHERE id = (?);"
            )) {
                statement.setString(1, user.username());
                statement.setString(2, user.password());
                statement.setString(3, user.name());
                statement.setString(4, user.role());
                statement.setBoolean(5, user.active());
                statement.setLong(6, Long.parseLong(user.id().id()));
                statement.execute();
            }
        });
    }

    @Override
    public void updateUserPartially(TransportUser user) {
        jdbcTemplate.executeCons(Connection.TRANSACTION_SERIALIZABLE, connection -> {
            checkIfExists(user.id(), user.username(), connection);
            try (var statement = connection.prepareStatement(
                    "UPDATE users_view SET " +
                            "user_name = (?), " +
                            "pass_word = (?), " +
                            "real_name = (?) " +
                            "WHERE id = (?);"
            )) {
                statement.setString(1, user.username());
                statement.setString(2, user.password());
                statement.setString(3, user.name());
                statement.setLong(4, Long.parseLong(user.id().id()));
                statement.execute();
            }
        });
    }

    @Override
    public void deleteUser(UserId userId) {
        jdbcTemplate.executeCons(Connection.TRANSACTION_READ_COMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "DELETE FROM users_view WHERE id = (?);"
            )) {
                statement.setLong(1, Long.parseLong(userId.id()));
                statement.execute();
            }
        });
    }

    private void checkIfExists(UserId userId, String username, Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement(
                "SELECT * FROM users_view WHERE user_name = (?);"
        )) {
            statement.setString(1, username);
            try (var resultSet = statement.executeQuery()) {
                var found = getUser(resultSet);
                if (found != null && !found.id().equals(userId)) {
                    throw new EntityAlreadyExistsException(String.format("User %s already exists", username));
                }
            }
        }
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        var result = (User) null;
        if (resultSet.next()) {
            var id = new UserId(String.valueOf(resultSet.getLong("id")));
            var username = resultSet.getString("user_name");
            var password = resultSet.getString("pass_word");
            var name = resultSet.getString("real_name");
            var role = resultSet.getString("user_role");
            var active = resultSet.getBoolean("is_active");
            result = new User(id, username, password, name, role, active);
        }
        return result;
    }
}
