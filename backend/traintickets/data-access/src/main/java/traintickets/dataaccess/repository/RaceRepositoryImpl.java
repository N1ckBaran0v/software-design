package traintickets.dataaccess.repository;

import traintickets.businesslogic.exception.TrainAlreadyReservedException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RaceRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
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
        jdbcTemplate.executeCons(carrierRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            var raceId = 0L;
            var startTime = new Timestamp(race.schedule().getFirst().departure().getTime());
            var endTime = new Timestamp(race.schedule().getLast().arrival().getTime());
            try (var statement = conn.prepareStatement(
                    "WITH bad_races AS (SELECT DISTINCT race_id FROM schedule " +
                            "WHERE departure >= (?) AND departure <= (?) " +
                            "OR arrival >= (?) AND arrival <= (?)) " +
                            "SELECT train_id FROM races WHERE id IN (SELECT * FROM bad_races) AND id = (?);"
            )) {
                statement.setTimestamp(1, startTime);
                statement.setTimestamp(2, endTime);
                statement.setTimestamp(3, startTime);
                statement.setTimestamp(4, endTime);
                statement.setLong(5, race.trainId().id());
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        throw new TrainAlreadyReservedException(race.trainId());
                    }
                }
            }
            try (var statement = conn.prepareStatement(
                    "INSERT INTO races (train_id, finished) VALUES (?, ?);",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setLong(1, race.trainId().id());
                statement.setBoolean(2, false);
                statement.executeUpdate();
                var rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    raceId = rs.getLong(1);
                }
            }
            try (var statement = conn.prepareStatement(
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
                    statement.executeUpdate();
                }
            }
        });
    }

    @Override
    public Optional<Race> getRace(RaceId raceId) {
        return jdbcTemplate.executeFunc(carrierRoleName, Connection.TRANSACTION_READ_COMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM races WHERE id = (?);"
            )) {
                statement.setLong(1, raceId.id());
                try (var resultSet = statement.executeQuery()) {
                    return Optional.ofNullable(getRace(conn, resultSet));
                }
            }
        });
    }

    @Override
    public Iterable<Race> getRaces(Filter filter) {
        return jdbcTemplate.executeFunc(carrierRoleName, Connection.TRANSACTION_READ_COMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
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
                    var race = getRace(conn, resultSet);
                    while (race != null) {
                        result.add(race);
                        race = getRace(conn, resultSet);
                    }
                    return result;
                }
            }
        });
    }

    @Override
    public void updateRace(Race race) {
        jdbcTemplate.executeCons(carrierRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            try (var statement = conn.prepareStatement(
                    "UPDATE races SET finished = (?) WHERE id = (?);"
            )) {
                statement.setBoolean(1, race.finished());
                statement.setLong(2, race.id().id());
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
            var schedule = new ArrayList<Schedule>();
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM schedule WHERE race_id = (?);"
            )) {
                statement.setLong(1, raceId.id());
                try (var resultSet2 = statement.executeQuery()) {
                    while (resultSet2.next()) {
                        var name = resultSet2.getString(3);
                        var arrival = resultSet2.getTimestamp(4);
                        var departure = resultSet2.getTimestamp(5);
                        var multiplier = resultSet2.getDouble(6);
                        schedule.add(new Schedule(name, arrival, departure, multiplier));
                    }
                }
            }
            answer = new Race(raceId, trainId, schedule, finished);
        }
        return answer;
    }
}
