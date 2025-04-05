package traintickets.dataaccess.repository;

import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.FilterRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public final class FilterRepositoryImpl implements FilterRepository {
    private final JdbcTemplate jdbcTemplate;

    public FilterRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
    }

    @Override
    public void addFilter(String role, Filter filter) {
        jdbcTemplate.executeCons(role, Connection.TRANSACTION_SERIALIZABLE, connection -> {
            checkIfExists(filter, connection);
            var filterId = saveFilter(filter, connection);
            savePassengers(filter, connection, filterId);
        });
    }

    private void checkIfExists(Filter filter, Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement(
                "SELECT * FROM filters where user_id = (?) AND filter_name = (?);"
        )) {
            statement.setLong(1, ((Number) filter.user().id()).longValue());
            statement.setString(2, filter.name());
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    throw new EntityAlreadyExistsException(String.format(
                            "Filter %s already exists", filter.name()));
                }
            }
        }
    }

    private long saveFilter(Filter filter, Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO filters (user_id, filter_name, departure, destination, transfers) " +
                        "VALUES (?, ?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setLong(1, ((Number) filter.user().id()).longValue());
            statement.setString(2, filter.name());
            statement.setString(3, filter.departure());
            statement.setString(4, filter.destination());
            statement.setInt(5, filter.transfers());
            statement.executeUpdate();
            var rs = statement.getGeneratedKeys();
            rs.next();
            return rs.getLong(1);
        }
    }

    private void savePassengers(Filter filter, Connection connection, long filterId) throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO passengers (filter_id, passengers_type, passengers_count) " +
                        "VALUES (?, ?, ?);"
        )) {
            for (var entry : filter.passengers().entrySet()) {
                statement.setLong(1, filterId);
                statement.setString(2, entry.getKey());
                statement.setInt(3, entry.getValue());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    @Override
    public Optional<Filter> getFilter(String role, UserId userId, String name) {
        return jdbcTemplate.executeFunc(role, Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM filters WHERE user_id = (?) AND filter_name = (?);"
            )) {
                statement.setLong(1, ((Number) userId.id()).longValue());
                statement.setString(2, name);
                try (var resultSet = statement.executeQuery()) {
                    return Optional.ofNullable(getFilter(connection, resultSet));
                }
            }
        });
    }

    @Override
    public Iterable<Filter> getFilters(String role, UserId userId) {
        return jdbcTemplate.executeFunc(role, Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM filters WHERE user_id = (?);"
            )) {
                statement.setLong(1, ((Number) userId.id()).longValue());
                try (var resultSet = statement.executeQuery()) {
                    var list = new ArrayList<Filter>();
                    var filter = getFilter(connection, resultSet);
                    while (filter != null) {
                        list.add(filter);
                        filter = getFilter(connection, resultSet);
                    }
                    return list;
                }
            }
        });
    }

    @Override
    public void deleteFilter(String role, UserId userId, String name) {
        jdbcTemplate.executeCons(role, Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "DELETE FROM filters WHERE user_id = (?) AND filter_name = (?);"
            )) {
                statement.setLong(1, ((Number) userId.id()).longValue());
                statement.setString(2, name);
                statement.execute();
            }
        });
    }

    private Filter getFilter(Connection connection, ResultSet resultSet) throws SQLException {
        var answer = (Filter) null;
        if (resultSet.next()) {
            var userId = new UserId(resultSet.getLong("user_id"));
            var name = resultSet.getString("filter_name");
            var departure = resultSet.getString("departure");
            var destination = resultSet.getString("destination");
            var transfers = resultSet.getInt("transfers");
            var passengers = new HashMap<String, Integer>();
            getPassengers(connection, resultSet.getLong("id"), passengers);
            answer = new Filter(userId, name, departure, destination, transfers, passengers, null, null);
        }
        return answer;
    }

    private void getPassengers(Connection connection, long filterId, Map<String, Integer> passengers)
            throws SQLException {
        try (var statement = connection.prepareStatement(
                "SELECT * FROM passengers WHERE filter_id = (?);"
        )) {
            statement.setLong(1, filterId);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var passengerType = resultSet.getString("passengers_type");
                    var passengerCount = resultSet.getInt("passengers_count");
                    passengers.put(passengerType, passengerCount);
                }
            }
        }
    }
}
