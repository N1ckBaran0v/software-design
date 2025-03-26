package traintickets.dataaccess.repository;

import traintickets.businesslogic.exception.FilterAlreadyExistsException;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.FilterRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public final class FilterRepositoryImpl implements FilterRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String userRoleName;

    public FilterRepositoryImpl(JdbcTemplate jdbcTemplate, String userRoleName) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.userRoleName = Objects.requireNonNull(userRoleName);
    }

    @Override
    public void addFilter(Filter filter) {
        jdbcTemplate.executeCons(userRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            var filterId = 0L;
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM filters where user_id = (?) AND filter_name = (?);"
            )) {
                statement.setLong(1, filter.user().id());
                statement.setString(2, filter.name());
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        throw new FilterAlreadyExistsException(filter.name());
                    }
                }
            }
            try (var statement = conn.prepareStatement(
                    "INSERT INTO filters (user_id, filter_name, departure, destination, train_class, transfers, min_cost, max_cost) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setLong(1, filter.user().id());
                statement.setString(2, filter.name());
                statement.setString(3, filter.departure());
                statement.setString(4, filter.destination());
                statement.setString(5, filter.trainClass());
                statement.setInt(6, filter.transfers());
                statement.setBigDecimal(7, filter.minCost());
                statement.setBigDecimal(8, filter.maxCost());
                statement.executeUpdate();
                var rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    filterId = rs.getLong(1);
                }
            }
            try (var statement = conn.prepareStatement(
                    "INSERT INTO passengers (filter_id, passengers_type, passengers_count) " +
                            "VALUES (?, ?, ?);"
            )) {
                var map = new HashMap<String, Integer>();
                filter.passengers().forEach(passenger -> map.put(passenger, map.getOrDefault(passenger, 0) + 1));
                for (var key : map.keySet()) {
                    statement.setLong(1, filterId);
                    statement.setString(2, key);
                    statement.setInt(3, map.get(key));
                    statement.executeUpdate();
                }
            }
        });
    }

    @Override
    public Optional<Filter> getFilter(UserId userId, String name) {
        return jdbcTemplate.executeFunc(userRoleName, Connection.TRANSACTION_READ_COMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM filters WHERE user_id = (?) AND filter_name = (?);"
            )) {
                statement.setLong(1, userId.id());
                statement.setString(2, name);
                try (var resultSet = statement.executeQuery()) {
                    return Optional.ofNullable(getFilter(conn, resultSet));
                }
            }
        });
    }

    @Override
    public Iterable<Filter> getFilters(UserId userId) {
        return jdbcTemplate.executeFunc(userRoleName, Connection.TRANSACTION_READ_COMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM filters WHERE user_id = (?);"
            )) {
                statement.setLong(1, userId.id());
                try (var resultSet = statement.executeQuery()) {
                    var list = new ArrayList<Filter>();
                    var filter = getFilter(conn, resultSet);
                    while (filter != null) {
                        list.add(filter);
                        filter = getFilter(conn, resultSet);
                    }
                    return list;
                }
            }
        });
    }

    @Override
    public void deleteFilter(UserId userId, String name) {
        jdbcTemplate.executeCons(userRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            try (var statement = conn.prepareStatement(
                    "DELETE FROM filters WHERE user_id = (?) AND filter_name = (?);"
            )) {
                statement.setLong(1, userId.id());
                statement.setString(2, name);
                statement.execute();
            }
        });
    }

    private Filter getFilter(Connection connection, ResultSet resultSet) throws SQLException {
        var answer = (Filter) null;
        if (resultSet.next()) {
            var userId = new UserId(resultSet.getLong(2));
            var name = resultSet.getString(3);
            var departure = resultSet.getString(4);
            var destination = resultSet.getString(5);
            var trainClass = resultSet.getString(6);
            var transfers = resultSet.getInt(7);
            var minCost = resultSet.getBigDecimal(8);
            var maxCost = resultSet.getBigDecimal(9);
            var passengers = new ArrayList<String>();
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM passengers WHERE filter_id = (?);"
            )) {
                statement.setLong(1, resultSet.getLong(1));
                try (var resultSet2 = statement.executeQuery()) {
                    while (resultSet2.next()) {
                        var passengerType = resultSet2.getString(3);
                        var passengerCount = resultSet2.getInt(4);
                        for (var i = 0; i < passengerCount; ++i) {
                            passengers.add(passengerType);
                        }
                    }
                }
            }
            answer = new Filter(userId, name, departure, destination, trainClass,
                    transfers, passengers, null, null, minCost, maxCost);
        }
        return answer;
    }
}
