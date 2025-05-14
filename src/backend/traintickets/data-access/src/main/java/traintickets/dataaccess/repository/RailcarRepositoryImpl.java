package traintickets.dataaccess.repository;

import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RailcarRepositoryImpl implements RailcarRepository {
    private final JdbcTemplate jdbcTemplate;

    public RailcarRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
    }

    @Override
    public void addRailcar(String role, Railcar railcar) {
        jdbcTemplate.executeCons(role, Connection.TRANSACTION_SERIALIZABLE, connection -> {
            checkIfExists(railcar, connection);
            var railcarId = saveRailcar(railcar, connection);
            savePlaces(railcar, connection, railcarId);
        });
    }

    private void checkIfExists(Railcar railcar, Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement(
                "SELECT * FROM railcars where railcar_model = (?);"
        )) {
            statement.setString(1, railcar.model());
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    throw new EntityAlreadyExistsException(String.format("Railcar %s already exists", railcar.model()));
                }
            }
        }
    }

    private long saveRailcar(Railcar railcar, Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO railcars (railcar_model, railcar_type) " +
                        "VALUES (?, ?);",
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, railcar.model());
            statement.setString(2, railcar.type());
            statement.executeUpdate();
            var rs = statement.getGeneratedKeys();
            rs.next();
            return rs.getLong("id");
        }
    }

    private void savePlaces(Railcar railcar, Connection connection, long railcarId) throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO places (railcar_id, place_number, description, purpose, place_cost) " +
                        "VALUES (?, ?, ?, ?, ?);"
        )) {
            for (var place : railcar.places()) {
                statement.setLong(1, railcarId);
                statement.setLong(2, place.number());
                statement.setString(3, place.description());
                statement.setString(4, place.purpose());
                statement.setBigDecimal(5, place.cost());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    @Override
    public Iterable<Railcar> getRailcarsByType(String role, String type) {
        return jdbcTemplate.executeFunc(role, Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM railcars where railcar_type = (?);"
            )) {
                statement.setString(1, type);
                return extractRailcars(connection, statement);
            }
        });
    }

    @Override
    public Iterable<Railcar> getRailcarsByTrain(String role, TrainId trainId) {
        return jdbcTemplate.executeFunc(role, Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "WITH railcars_ids AS (SELECT DISTINCT railcar_id FROM railcars_in_trains WHERE train_id = (?)) " +
                            "SELECT * FROM railcars WHERE id IN (SELECT * FROM railcars_ids);"
            )) {
                statement.setLong(1, Long.parseLong(trainId.id()));
                return extractRailcars(connection, statement);
            }
        });
    }

    private ArrayList<Railcar> extractRailcars(Connection conn, PreparedStatement statement) throws SQLException {
        try (var resultSet = statement.executeQuery()) {
            var result = new ArrayList<Railcar>();
            var railcar = getRailcar(conn, resultSet);
            while (railcar != null) {
                result.add(railcar);
                railcar = getRailcar(conn, resultSet);
            }
            return result;
        }
    }

    private Railcar getRailcar(Connection connection, ResultSet resultSet) throws SQLException {
        var answer = (Railcar) null;
        if (resultSet.next()) {
            var railcarId = new RailcarId(String.valueOf(resultSet.getLong("id")));
            var model = resultSet.getString("railcar_model");
            var type = resultSet.getString("railcar_type");
            var places = getPlaces(connection, railcarId);
            answer = new Railcar(railcarId, model, type, places);
        }
        return answer;
    }

    private List<Place> getPlaces(Connection connection, RailcarId railcarId) throws SQLException {
        var places = new ArrayList<Place>();
        try (var statement = connection.prepareStatement(
                "SELECT * FROM places WHERE railcar_id = (?);"
        )) {
            statement.setLong(1, Long.parseLong(railcarId.id()));
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var id = new PlaceId(String.valueOf(resultSet.getLong("id")));
                    var number = resultSet.getInt("place_number");
                    var description = resultSet.getString("description");
                    var purpose = resultSet.getString("purpose");
                    var cost = resultSet.getBigDecimal("place_cost");
                    places.add(new Place(id, number, description, purpose, cost));
                }
            }
        }
        return places;
    }
}
