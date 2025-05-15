package traintickets.dataaccess.repository;

import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.exception.PlaceAlreadyReservedException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.payment.PaymentData;
import traintickets.businesslogic.payment.PaymentManager;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TicketRepositoryImpl implements TicketRepository {
    private final JdbcTemplate jdbcTemplate;
    private final PaymentManager paymentManager;

    public TicketRepositoryImpl(JdbcTemplate jdbcTemplate, PaymentManager paymentManager) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.paymentManager = Objects.requireNonNull(paymentManager);
    }

    @Override
    public void addTickets(List<Ticket> tickets, PaymentData paymentData) {
        jdbcTemplate.executeCons(Connection.TRANSACTION_SERIALIZABLE, connection -> {
            try (var statement = connection.prepareStatement(
                    "INSERT INTO tickets (user_id, passenger, race_id, railcar, place_id, departure, destination, ticket_cost) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            )) {
                for (var ticket : tickets) {
                    checkIfCorrect(connection, ticket);
                    checkIfNotExists(connection, ticket);
                    statement.setLong(1, Long.parseLong(ticket.owner().id()));
                    statement.setString(2, ticket.passenger());
                    statement.setLong(3, Long.parseLong(ticket.race().id()));
                    statement.setInt(4, ticket.railcar());
                    statement.setLong(5, Long.parseLong(ticket.place().id().id()));
                    statement.setLong(6, Long.parseLong(ticket.start().id().id()));
                    statement.setLong(7, Long.parseLong(ticket.end().id().id()));
                    statement.setBigDecimal(8, ticket.cost());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
            paymentManager.pay(paymentData);
        });
    }

    private void checkIfCorrect(Connection connection, Ticket ticket) throws SQLException {
        var railcarId = 0L;
        var placeCost = BigDecimal.ZERO;
        try (var statement = connection.prepareStatement(
                "SELECT railcar_id, place_cost FROM places WHERE id = (?);"
        )) {
            statement.setLong(1, Long.parseLong(ticket.place().id().id()));
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    railcarId = resultSet.getLong("railcar_id");
                    placeCost = resultSet.getBigDecimal("place_cost");
                } else {
                    throw new InvalidEntityException("Place not found");
                }
            }
        }
        try (var  statement = connection.prepareStatement(
                "WITH curr_train_id AS (SELECT train_id FROM races WHERE id = (?)) " +
                        "SELECT * FROM railcars_in_trains WHERE train_id = (SELECT * FROM curr_train_id) LIMIT (?); "
        )) {
            statement.setLong(1, Long.parseLong(ticket.race().id()));
            statement.setInt(2, ticket.railcar());
            try (var resultSet = statement.executeQuery()) {
                for (var i = 0; i < ticket.railcar(); ++i) {
                    if (!resultSet.next()) {
                        throw new InvalidEntityException("Railcar not found");
                    }
                }
                if (resultSet.getLong("railcar_id") != railcarId) {
                    throw new InvalidEntityException("Invalid railcar");
                }
            }
        }
        try (var  statement = connection.prepareStatement(
                "SELECT * FROM schedule WHERE race_id = (?) AND id IN ((?), (?));"
        )) {
            statement.setLong(1, Long.parseLong(ticket.race().id()));
            statement.setLong(2, Long.parseLong(ticket.start().id().id()));
            statement.setLong(3, Long.parseLong(ticket.end().id().id()));
            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new InvalidEntityException("Departure not found");
                }
                var multiplier = BigDecimal.valueOf(resultSet.getDouble("multiplier"));
                if (!resultSet.next()) {
                    throw new InvalidEntityException("Destination not found");
                }
                multiplier = BigDecimal.valueOf(resultSet.getDouble("multiplier")).subtract(multiplier);
                placeCost = placeCost.multiply(multiplier);
                placeCost = placeCost.setScale(2, RoundingMode.HALF_UP);
                if (placeCost.compareTo(ticket.cost().setScale(2, RoundingMode.HALF_UP)) != 0) {
                    throw new InvalidEntityException("Invalid cost");
                }
            }
        }
    }

    private void checkIfNotExists(Connection connection, Ticket ticket) throws SQLException {
        try (var statement = connection.prepareStatement(
                "SELECT * FROM races WHERE id = (?) AND finished = FALSE;"
        )) {
            statement.setLong(1, Long.parseLong(ticket.race().id()));
            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new InvalidEntityException("Race already finished");
                }
            }
        }
        var startTimestamp = (Timestamp) null;
        var endTimestamp = (Timestamp) null;
        try (var statement = connection.prepareStatement(
                "SELECT arrival, departure FROM schedule WHERE id = (?) AND race_id = (?); "
        )) {
            statement.setLong(1, Long.parseLong(ticket.start().id().id()));
            statement.setLong(2, Long.parseLong(ticket.race().id()));
            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new InvalidEntityException("Schedule not found");
                }
                startTimestamp = resultSet.getTimestamp("departure");
            }
            statement.setLong(1, Long.parseLong(ticket.end().id().id()));
            statement.setLong(2, Long.parseLong(ticket.race().id()));
            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new InvalidEntityException("Schedule not found");
                }
                endTimestamp = resultSet.getTimestamp("arrival");
            }
        }
        if (startTimestamp == null || endTimestamp == null || startTimestamp.after(endTimestamp)) {
            throw new InvalidEntityException("Invalid schedule");
        }
        try (var statement = connection.prepareStatement(
                "WITH race_tickets AS (SELECT s1.departure as start_time, s2.arrival as end_time " +
                        "FROM tickets t JOIN schedule s1 ON t.departure = s1.id JOIN schedule s2 ON t.destination = s2.id " +
                        "WHERE t.race_id = (?) AND t.place_id = (?)) " +
                        "SELECT * FROM race_tickets WHERE " +
                        "start_time >= (?) AND start_time < (?) OR end_time > (?) AND end_time <= (?); "
        )) {
            statement.setLong(1, Long.parseLong(ticket.race().id()));
            statement.setLong(2, Long.parseLong(ticket.place().id().id()));
            statement.setTimestamp(3, startTimestamp);
            statement.setTimestamp(4, endTimestamp);
            statement.setTimestamp(5, startTimestamp);
            statement.setTimestamp(6, endTimestamp);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    throw new PlaceAlreadyReservedException(ticket.race(), ticket.railcar(), ticket.place().number());
                }
            }
        }
    }

    @Override
    public Iterable<Ticket> getTicketsByUser(UserId userId) {
        return jdbcTemplate.executeFunc(Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM tickets WHERE user_id = (?);"
            )) {
                statement.setLong(1, Long.parseLong(userId.id()));
                return getTickets(connection, statement);
            }
        });
    }

    @Override
    public Iterable<Ticket> getTicketsByRace(RaceId raceId) {
        return jdbcTemplate.executeFunc(Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM tickets WHERE race_id = (?);"
            )) {
                statement.setLong(1, Long.parseLong(raceId.id()));
                return getTickets(connection, statement);
            }
        });
    }

    private List<Ticket> getTickets(Connection connection, PreparedStatement statement) throws SQLException {
        try (var resultSet = statement.executeQuery()) {
            var tickets = new ArrayList<Ticket>();
            var ticket = getTicket(connection, resultSet);
            while (ticket != null) {
                tickets.add(ticket);
                ticket = getTicket(connection, resultSet);
            }
            return tickets;
        }
    }

    private Ticket getTicket(Connection connection, ResultSet resultSet) throws SQLException {
        var result = (Ticket) null;
        if (resultSet.next()) {
            var id = new TicketId(String.valueOf(resultSet.getLong("id")));
            var owner = new UserId(String.valueOf(resultSet.getLong("user_id")));
            var passenger = resultSet.getString("passenger");
            var race = new RaceId(String.valueOf(resultSet.getLong("race_id")));
            var railcar = resultSet.getInt("railcar");
            var place = getPlace(connection, resultSet.getLong("place_id"));
            var start = getSchedule(connection, resultSet.getLong("departure"));
            var end = getSchedule(connection, resultSet.getLong("destination"));
            var cost = resultSet.getBigDecimal("ticket_cost");
            result = new Ticket(id, owner, passenger, race, railcar, place, start, end, cost);
        }
        return result;
    }

    private Place getPlace(Connection connection, long placeId) throws SQLException {
        try (var statement = connection.prepareStatement(
                "SELECT * FROM places WHERE id = (?);"
        )) {
            statement.setLong(1, placeId);
            try (var resultSet = statement.executeQuery()) {
                resultSet.next();
                var id = new PlaceId(String.valueOf(resultSet.getLong("id")));
                var number = resultSet.getInt("place_number");
                var description = resultSet.getString("description");
                var purpose = resultSet.getString("purpose");
                var cost = resultSet.getBigDecimal("place_cost");
                return new Place(id, number, description, purpose, cost);
            }
        }
    }

    private Schedule getSchedule(Connection connection, long scheduleId) throws SQLException {
        try (var statement = connection.prepareStatement(
                "SELECT * FROM schedule WHERE id = (?);"
        )) {
            statement.setLong(1, scheduleId);
            try (var resultSet = statement.executeQuery()) {
                resultSet.next();
                var id = new ScheduleId(String.valueOf(resultSet.getLong("id")));
                var name = resultSet.getString("station_name");
                var arrival = resultSet.getTimestamp("arrival");
                var departure = resultSet.getTimestamp("departure");
                var multiplier = resultSet.getDouble("multiplier");
                return new Schedule(id, name, arrival, departure, multiplier);
            }
        }
    }
}
