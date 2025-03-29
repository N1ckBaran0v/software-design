package traintickets.dataaccess.repository;

import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

public final class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String systemRoleName;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate, String systemRoleName) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.systemRoleName = Objects.requireNonNull(systemRoleName);
    }

    @Override
    public void addUser(User user) {
        jdbcTemplate.executeCons(systemRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM users_view WHERE user_name = (?);"
            )) {
                statement.setString(1, user.username());
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        throw new EntityAlreadyExistsException(String.format(
                                "User %s already exists", user.username()));
                    }
                }
            }
            try (var statement = conn.prepareStatement(
                    "INSERT INTO users_view (user_name, pass_word, real_name, user_role, is_active)\n" +
                            "VALUES (?, ?, ?, ?, ?);"
            )) {
                statement.setString(1, user.username());
                statement.setString(2, user.password());
                statement.setString(3, user.name());
                statement.setString(4, user.role());
                statement.setBoolean(5, user.active());
                statement.execute();
            }
        });
    }

    @Override
    public Optional<User> getUser(String username) {
        return jdbcTemplate.executeFunc(systemRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            try (var statement = conn.prepareStatement(
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
        return jdbcTemplate.executeFunc(systemRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            var ids = StreamSupport.stream(userIds.spliterator(), false).map(id -> String.valueOf(id.id())).toList();
            try (var statement = conn.prepareStatement(
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
    public void updateUser(User user) {
        jdbcTemplate.executeCons(systemRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM users_view WHERE user_name = (?);"
            )) {
                statement.setString(1, user.username());
                try (var resultSet = statement.executeQuery()) {
                    var found = getUser(resultSet);
                    if (found != null && !found.id().equals(user.id())) {
                        throw new EntityAlreadyExistsException(String.format(
                                "User %s already exists", user.username()));
                    }
                }
            }
            try (var statement = conn.prepareStatement(
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
                statement.setLong(6, user.id().id());
                statement.execute();
            }
        });
    }

    @Override
    public void deleteUser(UserId userId) {
        jdbcTemplate.executeCons(systemRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            try (var statement = conn.prepareStatement(
                    "DELETE FROM users_view WHERE id = (?);"
            )) {
                statement.setLong(1, userId.id());
                statement.execute();
            }
        });
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        var result = (User) null;
        if (resultSet.next()) {
            var id = new UserId(resultSet.getLong("id"));
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
