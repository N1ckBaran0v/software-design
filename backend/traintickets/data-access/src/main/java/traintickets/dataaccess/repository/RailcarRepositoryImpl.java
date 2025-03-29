package traintickets.dataaccess.repository;

import traintickets.businesslogic.exception.EntityAlreadyExistsException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RailcarRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public final class RailcarRepositoryImpl implements RailcarRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String carrierRoleName;

    public RailcarRepositoryImpl(JdbcTemplate jdbcTemplate, String carrierRoleName) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.carrierRoleName = Objects.requireNonNull(carrierRoleName);
    }

    @Override
    public void addRailcar(Railcar railcar) {
        jdbcTemplate.executeCons(carrierRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            var railcarId = 0L;
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM railcars where railcar_model = (?);"
            )) {
                statement.setString(1, railcar.model());
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        throw new EntityAlreadyExistsException(String.format(
                                "Railcar %s already exists", railcar.model()));
                    }
                }
            }
            try (var statement = conn.prepareStatement(
                    "INSERT INTO railcars (railcar_model, railcar_type) " +
                            "VALUES (?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, railcar.model());
                statement.setString(2, railcar.type());
                statement.executeUpdate();
                var rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    railcarId = rs.getLong(1);
                }
            }
            try (var statement = conn.prepareStatement(
                    "INSERT INTO places (railcar_id, place_number, description, purpose, place_cost) " +
                            "VALUES (?, ?, ?, ?, ?);"
            )) {
                for (var place : railcar.places()) {
                    statement.setLong(1, railcarId);
                    statement.setLong(2, place.number());
                    statement.setString(3, place.description());
                    statement.setString(4, place.purpose());
                    statement.setBigDecimal(5, place.cost());
                    statement.executeUpdate();
                }
            }
        });
    }

    @Override
    public Iterable<Railcar> getRailcarsByType(String type) {
        return jdbcTemplate.executeFunc(carrierRoleName, Connection.TRANSACTION_READ_COMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM railcars where railcar_type = (?);"
            )) {
                statement.setString(1, type);
                return extractRailcars(conn, statement);
            }
        });
    }

    @Override
    public Iterable<Railcar> getRailcarsByTrain(TrainId trainId) {
        return jdbcTemplate.executeFunc(carrierRoleName, Connection.TRANSACTION_READ_COMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "WITH railcars_ids AS (SELECT DISTINCT railcar_id FROM railcarsintrains WHERE train_id = (?)) " +
                            "SELECT * FROM railcars WHERE id IN (SELECT * FROM railcars_ids);"
            )) {
                statement.setLong(1, trainId.id());
                return extractRailcars(conn, statement);
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
            var railcarId = new RailcarId(resultSet.getLong(1));
            var model = resultSet.getString(2);
            var type = resultSet.getString(3);
            var places = new ArrayList<Place>();
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM places WHERE railcar_id = (?);"
            )) {
                statement.setLong(1, railcarId.id());
                try (var resultSet2 = statement.executeQuery()) {
                    while (resultSet2.next()) {
                        var number = resultSet2.getInt(3);
                        var description = resultSet2.getString(4);
                        var purpose = resultSet2.getString(5);
                        var cost = resultSet2.getBigDecimal(6);
                        places.add(new Place(number, description, purpose, cost));
                    }
                }
            }
            answer = new Railcar(railcarId, model, type, places);
        }
        return answer;
    }
}
