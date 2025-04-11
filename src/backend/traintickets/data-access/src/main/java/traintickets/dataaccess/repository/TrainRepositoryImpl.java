package traintickets.dataaccess.repository;

import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.TrainRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.*;
import java.util.*;
import java.util.Date;

public final class TrainRepositoryImpl implements TrainRepository {
//    private final JdbcTemplate jdbcTemplate;
//
//    public TrainRepositoryImpl(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
//    }
//
//    @Override
//    public void addTrain(String role, Train train) {
//        jdbcTemplate.executeCons(role, Connection.TRANSACTION_SERIALIZABLE, connection -> {
//            var trainId = saveTrain(train, connection);
//            saveRailcars(train, connection, trainId);
//        });
//    }
//
//    private long saveTrain(Train train, Connection connection) throws SQLException {
//        try (var statement = connection.prepareStatement(
//                "INSERT INTO trains (train_class) VALUES (?);",
//                Statement.RETURN_GENERATED_KEYS
//        )) {
//            statement.setString(1, train.trainClass());
//            statement.executeUpdate();
//            var rs = statement.getGeneratedKeys();
//            rs.next();
//            return rs.getLong("id");
//        }
//    }
//
//    private void saveRailcars(Train train, Connection connection, long trainId) throws SQLException {
//        try (var statement = connection.prepareStatement(
//                "INSERT INTO railcarsintrains (train_id, railcar_id) VALUES (?, ?);"
//        )) {
//            for (var railcar : train.railcars()) {
//                statement.setLong(1, trainId);
//                statement.setLong(2, ((Number) railcar.id()).longValue());
//                statement.addBatch();
//            }
//            statement.executeBatch();
//        }
//    }
//
//    @Override
//    public Optional<Train> getTrain(String role, TrainId trainId) {
//        return jdbcTemplate.executeFunc(role, Connection.TRANSACTION_REPEATABLE_READ, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM trains WHERE id = (?);"
//            )) {
//                statement.setLong(1, ((Number) trainId.id()).longValue());
//                try (var resultSet = statement.executeQuery()) {
//                    return Optional.ofNullable(getTrain(connection, resultSet));
//                }
//            }
//        });
//    }
//
//    @Override
//    public Iterable<Train> getTrains(String role, Date start, Date end) {
//        return jdbcTemplate.executeFunc(role, Connection.TRANSACTION_REPEATABLE_READ, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "WITH bad_races AS (SELECT DISTINCT race_id FROM schedule " +
//                            "WHERE departure > (?) AND departure < (?) " +
//                            "OR arrival > (?) AND arrival < (?)), " +
//                            "bad_ids as (SELECT train_id FROM races WHERE id IN (SELECT * FROM bad_races)) " +
//                            "SELECT * FROM trains WHERE id NOT IN (SELECT * FROM bad_ids);"
//            )) {
//                var startTimestamp = new Timestamp(start.getTime());
//                var endTimestamp = new Timestamp(end.getTime());
//                statement.setTimestamp(1, startTimestamp);
//                statement.setTimestamp(2, endTimestamp);
//                statement.setTimestamp(3, startTimestamp);
//                statement.setTimestamp(4, endTimestamp);
//                try (var resultSet = statement.executeQuery()) {
//                    var result = new ArrayList<Train>();
//                    var train = getTrain(resultSet);
//                    while (train != null) {
//                        result.add(train);
//                        train = getTrain(resultSet);
//                    }
//                    return result;
//                }
//            }
//        });
//    }
//
//    private Train getTrain(Connection connection, ResultSet resultSet) throws SQLException {
//        var answer = (Train) null;
//        if (resultSet.next()) {
//            var trainId = new TrainId(resultSet.getLong("id"));
//            var trainClass = resultSet.getString("train_class");
//            var railcarIds = getRailcarIds(connection, trainId);
//            answer = new Train(trainId, trainClass, railcarIds);
//        }
//        return answer;
//    }
//
//    private List<RailcarId> getRailcarIds(Connection connection, TrainId trainId) throws SQLException {
//        var railcarIds = new ArrayList<RailcarId>();
//        try (var statement = connection.prepareStatement(
//                "SELECT * FROM railcarsintrains WHERE train_id = (?);"
//        )) {
//            statement.setLong(1, ((Number) trainId.id()).longValue());
//            try (var resultSet = statement.executeQuery()) {
//                while (resultSet.next()) {
//                    railcarIds.add(new RailcarId(resultSet.getLong("railcar_id")));
//                }
//            }
//        }
//        return railcarIds;
//    }
//
//    private Train getTrain(ResultSet resultSet) throws SQLException {
//        var answer = (Train) null;
//        if (resultSet.next()) {
//            var trainId = new TrainId(resultSet.getLong("id"));
//            var trainClass = resultSet.getString("train_class");
//            answer = new Train(trainId, trainClass, List.of());
//        }
//        return answer;
//    }
}
