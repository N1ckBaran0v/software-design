package traintickets.dataaccess.repository;

import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.TrainRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.*;
import java.util.*;
import java.util.Date;

public final class TrainRepositoryImpl implements TrainRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String carrierRoleName;

    public TrainRepositoryImpl(JdbcTemplate jdbcTemplate, String carrierRoleName) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.carrierRoleName = Objects.requireNonNull(carrierRoleName);
    }

    @Override
    public void addTrain(Train train) {
        jdbcTemplate.executeCons(carrierRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            var trainId = 0L;
            try (var statement = conn.prepareStatement(
                    "INSERT INTO trains (train_class) VALUES (?);",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, train.trainClass());
                statement.executeUpdate();
                var rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    trainId = rs.getLong(1);
                }
            }
            try (var statement = conn.prepareStatement(
                    "INSERT INTO railcarsintrains (train_id, railcar_id) VALUES (?, ?);"
            )) {
                for (var railcar : train.railcars()) {
                    statement.setLong(1, trainId);
                    statement.setLong(2, railcar.id());
                    statement.executeUpdate();
                }
            }
        });
    }

    @Override
    public Optional<Train> getTrain(TrainId trainId) {
        return jdbcTemplate.executeFunc(carrierRoleName, Connection.TRANSACTION_READ_COMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM trains WHERE id = (?);"
            )) {
                statement.setLong(1, trainId.id());
                try (var resultSet = statement.executeQuery()) {
                    return Optional.ofNullable(getTrain(conn, resultSet));
                }
            }
        });
    }

    @Override
    public Iterable<Train> getTrains(Date start, Date end) {
        return jdbcTemplate.executeFunc(carrierRoleName, Connection.TRANSACTION_READ_COMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "WITH bad_races AS (SELECT DISTINCT race_id FROM schedule " +
                            "WHERE departure > (?) AND departure < (?) " +
                            "OR arrival > (?) AND arrival < (?)), " +
                            "bad_ids as (SELECT train_id FROM races WHERE id IN (SELECT * FROM bad_races)) " +
                            "SELECT * FROM trains WHERE id NOT IN (SELECT * FROM bad_ids);"
            )) {
                var startTimestamp = new Timestamp(start.getTime());
                var endTimestamp = new Timestamp(end.getTime());
                statement.setTimestamp(1, startTimestamp);
                statement.setTimestamp(2, endTimestamp);
                statement.setTimestamp(3, startTimestamp);
                statement.setTimestamp(4, endTimestamp);
                try (var resultSet = statement.executeQuery()) {
                    var result = new ArrayList<Train>();
                    var train = getTrain(resultSet);
                    while (train != null) {
                        result.add(train);
                        train = getTrain(resultSet);
                    }
                    return result;
                }
            }
        });
    }

    private Train getTrain(Connection connection, ResultSet resultSet) throws SQLException {
        var answer = (Train) null;
        if (resultSet.next()) {
            var trainId = new TrainId(resultSet.getLong(1));
            var trainClass = resultSet.getString(2);
            var railcarIds = new ArrayList<RailcarId>();
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM railcarsintrains WHERE train_id = (?);"
            )) {
                statement.setLong(1, trainId.id());
                try (var resultSet2 = statement.executeQuery()) {
                    while (resultSet2.next()) {
                        railcarIds.add(new RailcarId(resultSet2.getLong(1)));
                    }
                }
            }
            answer = new Train(trainId, trainClass, railcarIds);
        }
        return answer;
    }

    private Train getTrain(ResultSet resultSet) throws SQLException {
        var answer = (Train) null;
        if (resultSet.next()) {
            var trainId = new TrainId(resultSet.getLong(1));
            var trainClass = resultSet.getString(2);
            answer = new Train(trainId, trainClass, List.of());
        }
        return answer;
    }
}
