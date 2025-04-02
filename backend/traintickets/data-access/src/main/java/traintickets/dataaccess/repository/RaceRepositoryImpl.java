package traintickets.dataaccess.repository;

import traintickets.businesslogic.exception.TrainAlreadyReservedException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RaceRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class RaceRepositoryImpl implements RaceRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String carrierRoleName;

    public RaceRepositoryImpl(JdbcTemplate jdbcTemplate, String carrierRoleName) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.carrierRoleName = Objects.requireNonNull(carrierRoleName);
    }

    @Override
    public void addRace(Race race) {
        jdbcTemplate.executeCons(carrierRoleName, Connection.TRANSACTION_SERIALIZABLE, connection -> {
            var startTime = new Timestamp(race.schedule().getFirst().departure().getTime());
            var endTime = new Timestamp(race.schedule().getLast().arrival().getTime());
            checkIfReserved(race, connection, startTime, endTime);
            var raceId = saveRace(race, connection);
            saveSchedule(race, connection, raceId);
        });
    }

    private void checkIfReserved(Race race, Connection connection, Timestamp startTime, Timestamp endTime)
            throws SQLException {
        try (var statement = connection.prepareStatement(
                "WITH bad_races AS (SELECT DISTINCT race_id FROM schedule " +
                        "WHERE departure >= (?) AND departure <= (?) " +
                        "OR arrival >= (?) AND arrival <= (?)) " +
                        "SELECT train_id FROM races WHERE id IN (SELECT * FROM bad_races) AND id = (?);"
        )) {
            statement.setTimestamp(1, startTime);
            statement.setTimestamp(2, endTime);
            statement.setTimestamp(3, startTime);
            statement.setTimestamp(4, endTime);
            statement.setLong(5, ((Number) race.trainId().id()).longValue());
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    throw new TrainAlreadyReservedException(race.trainId());
                }
            }
        }
    }

    private long saveRace(Race race, Connection connection) throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO races (train_id, finished) VALUES (?, ?);",
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setLong(1, ((Number) race.trainId().id()).longValue());
            statement.setBoolean(2, false);
            statement.executeUpdate();
            var rs = statement.getGeneratedKeys();
            rs.next();
            return rs.getLong(1);
        }
    }

    private void saveSchedule(Race race, Connection connection, long raceId) throws SQLException {
        try (var statement = connection.prepareStatement(
                "INSERT INTO schedule (race_id, station_name, arrival, departure, multiplier) " +
                        "VALUES (?, ?, ?, ?, ?);"
        )) {
            for (var schedule : race.schedule()) {
                statement.setLong(1, raceId);
                statement.setString(2, schedule.name());
                statement.setTimestamp(3,
                        schedule.arrival() == null ? null : new Timestamp(schedule.arrival().getTime()));
                statement.setTimestamp(4,
                        schedule.departure() == null ? null : new Timestamp(schedule.departure().getTime()));
                statement.setDouble(5, schedule.multiplier());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    @Override
    public Optional<Race> getRace(RaceId raceId) {
        return jdbcTemplate.executeFunc(carrierRoleName, Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM races WHERE id = (?);"
            )) {
                statement.setLong(1, ((Number) raceId.id()).longValue());
                try (var resultSet = statement.executeQuery()) {
                    return Optional.ofNullable(getRace(connection, resultSet));
                }
            }
        });
    }

    @Override
    public Iterable<Race> getRaces(Filter filter) {
        return jdbcTemplate.executeFunc(carrierRoleName, Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "WITH races_time AS (SELECT race_id, MIN(departure) as start_time, MAX(arrival) as end_time " +
                            "FROM schedule GROUP BY race_id), " +
                            "good_races AS (SELECT race_id FROM races_time WHERE " +
                            "start_time >= (?) AND end_time <= (?)) " +
                            "SELECT * FROM races WHERE id IN (SELECT * FROM good_races);"
            )) {
                statement.setTimestamp(1, new Timestamp(filter.start().getTime()));
                statement.setTimestamp(2, new Timestamp(filter.end().getTime()));
                try (var resultSet = statement.executeQuery()) {
                    var result = new ArrayList<Race>();
                    var race = getRace(connection, resultSet);
                    while (race != null) {
                        result.add(race);
                        race = getRace(connection, resultSet);
                    }
                    return result;
                }
            }
        });
    }

    @Override
    public void updateRace(Race race) {
        jdbcTemplate.executeCons(carrierRoleName, Connection.TRANSACTION_READ_COMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "UPDATE races SET finished = (?) WHERE id = (?);"
            )) {
                statement.setBoolean(1, race.finished());
                statement.setLong(2, ((Number) race.id().id()).longValue());
                statement.executeUpdate();
            }
        });
    }

    private Race getRace(Connection connection, ResultSet resultSet) throws SQLException {
        var answer = (Race) null;
        if (resultSet.next()) {
            var raceId = new RaceId(resultSet.getLong(1));
            var trainId = new TrainId(resultSet.getLong(2));
            var finished = resultSet.getBoolean(3);
            var schedule = getSchedule(connection, raceId);
            answer = new Race(raceId, trainId, schedule, finished);
        }
        return answer;
    }

    private List<Schedule> getSchedule(Connection connection, RaceId raceId) throws SQLException {
        var schedule = new ArrayList<Schedule>();
        try (var statement = connection.prepareStatement(
                "SELECT * FROM schedule WHERE race_id = (?);"
        )) {
            statement.setLong(1, ((Number) raceId.id()).longValue());
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var id = new ScheduleId(resultSet.getLong(1));
                    var name = resultSet.getString(3);
                    var arrival = resultSet.getTimestamp(4);
                    var departure = resultSet.getTimestamp(5);
                    var multiplier = resultSet.getDouble(6);
                    schedule.add(new Schedule(id, name, arrival, departure, multiplier));
                }
            }
        }
        return schedule;
    }
}
