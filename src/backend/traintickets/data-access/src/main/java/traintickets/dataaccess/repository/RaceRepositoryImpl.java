package traintickets.dataaccess.repository;

import traintickets.businesslogic.exception.TrainAlreadyReservedException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.repository.RaceRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.*;
import java.util.*;

public final class RaceRepositoryImpl implements RaceRepository {
    private final JdbcTemplate jdbcTemplate;

    public RaceRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
    }

    @Override
    public void addRace(Race race) {
        jdbcTemplate.executeCons(Connection.TRANSACTION_SERIALIZABLE, connection -> {
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
            statement.setLong(5, Long.parseLong(race.trainId().id()));
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
            statement.setLong(1, Long.parseLong(race.trainId().id()));
            statement.setBoolean(2, false);
            statement.executeUpdate();
            var rs = statement.getGeneratedKeys();
            rs.next();
            return rs.getLong("id");
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
        return jdbcTemplate.executeFunc(Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM races WHERE id = (?);"
            )) {
                statement.setLong(1, Long.parseLong(raceId.id()));
                try (var resultSet = statement.executeQuery()) {
                    return Optional.ofNullable(getRace(connection, resultSet));
                }
            }
        });
    }

    @Override
    public List<Race> getRaces(Filter filter) {
        return jdbcTemplate.executeFunc(Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM schedule WHERE (arrival >= (?) AND arrival <= (?)) OR " +
                            "(departure >= (?) AND departure <= (?));"
            )) {
                var start = new Timestamp(filter.start().getTime());
                var end = new Timestamp(filter.end().getTime());
                statement.setTimestamp(1, start);
                statement.setTimestamp(2, end);
                statement.setTimestamp(3, start);
                statement.setTimestamp(4, end);
                try (var resultSet = statement.executeQuery()) {
                    var result = getSchedules(resultSet);
                    return filterSchedules(connection, result, filter);
                }
            }
        });
    }

    @Override
    public void updateRace(RaceId raceId, boolean isFinished) {
        jdbcTemplate.executeCons(Connection.TRANSACTION_READ_COMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "UPDATE races SET finished = (?) WHERE id = (?);"
            )) {
                statement.setBoolean(1, isFinished);
                statement.setLong(2, Long.parseLong(raceId.id()));
                statement.executeUpdate();
            }
        });
    }

    private Race getRace(Connection connection, ResultSet resultSet) throws SQLException {
        var answer = (Race) null;
        if (resultSet.next()) {
            var raceId = new RaceId(String.valueOf(resultSet.getLong("id")));
            var trainId = new TrainId(String.valueOf(resultSet.getLong("train_id")));
            var finished = resultSet.getBoolean("finished");
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
            statement.setLong(1, Long.parseLong(raceId.id()));
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    schedule.add(getSchedule(resultSet));
                }
            }
        }
        return schedule;
    }

    private Map<RaceId, List<Schedule>> getSchedules(ResultSet resultSet) throws SQLException {
        var result = new HashMap<RaceId, List<Schedule>>();
        while (resultSet.next()) {
            var raceId = new RaceId(String.valueOf(resultSet.getLong("race_id")));
            if (!result.containsKey(raceId)) {
                result.put(raceId, new ArrayList<>());
            }
            result.get(raceId).add(getSchedule(resultSet));
        }
        return result;
    }

    private Schedule getSchedule(ResultSet resultSet) throws SQLException {
        var id = new ScheduleId(String.valueOf(resultSet.getLong("id")));
        var name = resultSet.getString("station_name");
        var arrival = resultSet.getTimestamp("arrival");
        var departure = resultSet.getTimestamp("departure");
        var multiplier = resultSet.getDouble("multiplier");
        return new Schedule(id, name, arrival, departure, multiplier);
    }

    private List<Race> filterSchedules(Connection connection,
                                       Map<RaceId, List<Schedule>> schedules,
                                       Filter filter) throws SQLException {
        var result = new ArrayList<Race>();
        if (filter.transfers() == 0) {
            for (var entry : schedules.entrySet()) {
                if (containsStations(entry.getValue(), filter.departure(), filter.destination()) == 2) {
                    addRace(connection, result, entry);
                }
            }
        } else if (filter.transfers() == 1) {
            for (var entry : schedules.entrySet()) {
                if (containsStations(entry.getValue(), filter.departure(), filter.destination()) > 0) {
                    addRace(connection, result, entry);
                }
            }
        } else {
            for (var entry : schedules.entrySet()) {
                if (entry.getValue().size() > 1) {
                    addRace(connection, result, entry);
                }
            }
        }
        return result;
    }

    private int containsStations(List<Schedule> schedules, String... stations) {
        var found = new HashSet<String>();
        if (schedules.size() > 1) {
            for (var schedule : schedules) {
                for (var station : stations) {
                    if (station.equals(schedule.name())) {
                        found.add(station);
                    }
                }
            }
        }
        return found.size();
    }

    private void addRace(Connection connection,
                         List<Race> races,
                         Map.Entry<RaceId, List<Schedule>> entry) throws SQLException {
        try (var statement = connection.prepareStatement(
                "SELECT * FROM races WHERE id = (?) and finished = FALSE;"
        )) {
            statement.setLong(1, Long.parseLong(entry.getKey().id()));
            try (var resultSet = statement.executeQuery()) {
                var race = getRace(resultSet, entry.getValue());
                if (race != null) {
                    races.add(race);
                }
            }
        }
    }

    private Race getRace(ResultSet resultSet, List<Schedule> schedule) throws SQLException {
        var answer = (Race) null;
        if (resultSet.next()) {
            var raceId = new RaceId(String.valueOf(resultSet.getLong("id")));
            var trainId = new TrainId(String.valueOf(resultSet.getLong("train_id")));
            var finished = resultSet.getBoolean("finished");
            answer = new Race(raceId, trainId, schedule, finished);
        }
        return answer;
    }
}
