package traintickets.dataaccess.repository;

import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.exception.PlaceAlreadyReservedException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.payment.PaymentData;
import traintickets.businesslogic.payment.PaymentManager;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TicketRepositoryImpl implements TicketRepository {
    private final JdbcTemplate jdbcTemplate;
    private final PaymentManager paymentManager;
    private final String carrierRoleName;

    public TicketRepositoryImpl(JdbcTemplate jdbcTemplate, PaymentManager paymentManager, String carrierRoleName) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.paymentManager = Objects.requireNonNull(paymentManager);
        this.carrierRoleName = Objects.requireNonNull(carrierRoleName);
    }

    @Override
    public void addTickets(List<Ticket> tickets, PaymentData paymentData) {
        jdbcTemplate.executeCons(carrierRoleName, Connection.TRANSACTION_SERIALIZABLE, connection -> {
            try (var statement = connection.prepareStatement(
                    "INSERT INTO tickets (user_id, passenger, race_id, railcar, place_id, departure, destination, ticket_cost) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            )) {
                for (var ticket : tickets) {
                    checkIfCorrect(connection, ticket);
                    checkIfNotExists(connection, ticket);
                    statement.setLong(1, ticket.owner().id());
                    statement.setString(2, ticket.passenger());
                    statement.setLong(3, ticket.race().id());
                    statement.setInt(4, ticket.railcar());
                    statement.setLong(5, ticket.place().id().id());
                    statement.setLong(6, ticket.start().id().id());
                    statement.setLong(7, ticket.end().id().id());
                    statement.setBigDecimal(8, ticket.cost());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
            paymentManager.pay(paymentData);
        });
    }

    private void checkIfCorrect(Connection connection, Ticket ticket) throws SQLException {
        try (var  statement = connection.prepareStatement(
                "WITH curr_train_id AS (SELECT train_id FROM races WHERE id = (?)), " +
                        "curr_railcar_id AS (SELECT railcar_id FROM places WHERE id = (?)) " +
                        "SELECT * FROM railcarsintrains WHERE train_id = (SELECT * FROM curr_train_id) " +
                        "AND railcar_id = (SELECT * FROM curr_railcar_id); "
        )) {
            statement.setLong(1, ticket.race().id());
            statement.setLong(2, ticket.place().id().id());
            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new InvalidEntityException("Invalid place in ticket");
                }
            }
        }
        try (var  statement = connection.prepareStatement(
                "SELECT * FROM schedule WHERE race_id = (?) AND id IN ((?), (?));"
        )) {
            statement.setLong(1, ticket.race().id());
            statement.setLong(2, ticket.start().id().id());
            statement.setLong(3, ticket.end().id().id());
            try (var resultSet = statement.executeQuery()) {
                if (!(resultSet.next() && resultSet.next())) {
                    throw new InvalidEntityException("Invalid schedule in ticket");
                }
            }
        }
    }

    private void checkIfNotExists(Connection connection, Ticket ticket) throws SQLException {
        var startTimestamp = new Timestamp(ticket.start().departure().getTime());
        var endTimestamp = new Timestamp(ticket.end().arrival().getTime());
        try (var statement = connection.prepareStatement(
                "WITH race_tickets AS (SELECT s1.departure as start_time, s2.arrival as end_time " +
                        "FROM tickets t JOIN schedule s1 ON t.departure = s1.id JOIN schedule s2 ON t.destination = s2.id " +
                        "WHERE t.race_id = (?) AND t.place_id = (?)) " +
                        "SELECT * FROM race_tickets WHERE " +
                        "start_time >= (?) AND start_time < (?) OR end_time > (?) AND end_time <= (?); "
        )) {
            statement.setLong(1, ticket.race().id());
            statement.setLong(2, ticket.place().id().id());
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
        return jdbcTemplate.executeFunc(carrierRoleName, Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM tickets WHERE user_id = (?);"
            )) {
                statement.setLong(1, userId.id());
                return getTickets(connection, statement);
            }
        });
    }

    @Override
    public Iterable<Ticket> getTicketsByRace(RaceId raceId) {
        return jdbcTemplate.executeFunc(carrierRoleName, Connection.TRANSACTION_REPEATABLE_READ, connection -> {
            try (var statement = connection.prepareStatement(
                    "SELECT * FROM tickets WHERE race_id = (?);"
            )) {
                statement.setLong(1, raceId.id());
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
            var id = new TicketId(resultSet.getLong(1));
            var owner = new UserId(resultSet.getLong(2));
            var passenger = resultSet.getString(3);
            var race = new RaceId(resultSet.getLong(4));
            var railcar = resultSet.getInt(5);
            var place = getPlace(connection, resultSet.getLong(6));
            var start = getSchedule(connection, resultSet.getLong(7));
            var end = getSchedule(connection, resultSet.getLong(8));
            var cost = resultSet.getBigDecimal(9);
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
                var id = new PlaceId(resultSet.getLong(1));
                var number = resultSet.getInt(3);
                var description = resultSet.getString(4);
                var purpose = resultSet.getString(5);
                var cost = resultSet.getBigDecimal(6);
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
                var id = new ScheduleId(resultSet.getLong(1));
                var name = resultSet.getString(3);
                var arrival = resultSet.getTimestamp(4);
                var departure = resultSet.getTimestamp(5);
                var multiplier = resultSet.getDouble(6);
                return new Schedule(id, name, arrival, departure, multiplier);
            }
        }
    }
}
