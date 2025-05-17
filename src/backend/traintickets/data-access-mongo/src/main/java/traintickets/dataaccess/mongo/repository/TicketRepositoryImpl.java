package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.exception.PlaceAlreadyReservedException;
import traintickets.businesslogic.model.*;
import traintickets.businesslogic.payment.PaymentData;
import traintickets.businesslogic.payment.PaymentManager;
import traintickets.businesslogic.repository.TicketRepository;
import traintickets.dataaccess.mongo.connection.MongoExecutor;
import traintickets.dataaccess.mongo.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;

public final class TicketRepositoryImpl implements TicketRepository {
    private final MongoExecutor mongoExecutor;
    private final MongoCollection<TicketDocument> ticketCollection;
    private final MongoCollection<PlaceDocument> placeCollection;
    private final MongoCollection<RaceDocument> raceCollection;
    private final MongoCollection<TrainDocument> trainCollection;
    private final MongoCollection<ScheduleDocument> scheduleCollection;

    private final PaymentManager paymentManager;

    public TicketRepositoryImpl(MongoExecutor mongoExecutor, PaymentManager paymentManager) {
        this.mongoExecutor = mongoExecutor;
        ticketCollection = mongoExecutor.getDatabase().getCollection("tickets", TicketDocument.class);
        placeCollection = mongoExecutor.getDatabase().getCollection("places", PlaceDocument.class);
        raceCollection = mongoExecutor.getDatabase().getCollection("races", RaceDocument.class);
        trainCollection = mongoExecutor.getDatabase().getCollection("trains", TrainDocument.class);
        scheduleCollection = mongoExecutor.getDatabase().getCollection("schedules", ScheduleDocument.class);
        this.paymentManager = paymentManager;
    }

    @Override
    public void addTickets(List<Ticket> tickets, PaymentData paymentData) {
        mongoExecutor.transactionConsumer(session -> {
            ticketCollection.insertMany(session, tickets.stream().map(ticket -> {
                checkIfCorrect(session, ticket);
                return new TicketDocument(ticket);
            }).toList());
            paymentManager.pay(paymentData);
        });
    }

//    @Override
//    public void addTickets(List<Ticket> tickets, PaymentData paymentData) {
//        jdbcTemplate.executeCons(Connection.TRANSACTION_SERIALIZABLE, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "INSERT INTO tickets (user_id, passenger, race_id, railcar, place_id, departure, destination, ticket_cost) " +
//                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
//            )) {
//                for (var ticket : tickets) {
//                    checkIfCorrect(connection, ticket);
//                    checkIfNotExists(connection, ticket);
//                    statement.setLong(1, Long.parseLong(ticket.owner().id()));
//                    statement.setString(2, ticket.passenger());
//                    statement.setLong(3, Long.parseLong(ticket.race().id()));
//                    statement.setInt(4, ticket.railcar());
//                    statement.setLong(5, Long.parseLong(ticket.place().id().id()));
//                    statement.setLong(6, Long.parseLong(ticket.start().id().id()));
//                    statement.setLong(7, Long.parseLong(ticket.end().id().id()));
//                    statement.setBigDecimal(8, ticket.cost());
//                    statement.addBatch();
//                }
//                statement.executeBatch();
//            }
//            paymentManager.pay(paymentData);
//        });
//    }

    private void checkIfCorrect(ClientSession session, Ticket ticket) {
        var place = placeCollection.find(session, Filters.eq("_id", new ObjectId(ticket.place().id().id()))).first();
        if (place == null) {
            throw new InvalidEntityException("Place not found");
        }
        var railcarId = place.railcarId();
        var placeCost = place.cost();
        var race = raceCollection.find(session, Filters.eq("_id", new ObjectId(ticket.race().id()))).first();
        if (race == null) {
            throw new InvalidEntityException("Race not found");
        }
        var train = trainCollection.find(session, Filters.eq("_id", race.trainId())).first();
        if (train == null) {
            throw new InvalidEntityException("Train not found");
        }
        if (train.railcars().size() <= ticket.railcar()) {
            throw new InvalidEntityException("Railcar not found");
        }
        var realRailcarId = train.railcars().get(ticket.railcar() - 1);
        if (!realRailcarId.equals(railcarId)) {
            throw new InvalidEntityException("Invalid railcar");
        }
        var start = scheduleCollection.find(session, Filters.eq("_id", new ObjectId(ticket.start().id().id()))).first();
        if (start == null) {
            throw new InvalidEntityException("Start not found");
        }
        var end = scheduleCollection.find(session, Filters.eq("_id", new ObjectId(ticket.end().id().id()))).first();
        if (end == null) {
            throw new InvalidEntityException("End not found");
        }
        var multiplier = BigDecimal.valueOf(end.multiplier()).subtract(BigDecimal.valueOf(start.multiplier()));
        placeCost = placeCost.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        if (placeCost.compareTo(ticket.cost().setScale(2, RoundingMode.HALF_UP)) != 0) {
            throw new InvalidEntityException("Invalid cost");
        }
        var startTime = start.departure();
        var endTime = end.arrival();
        if (startTime == null || endTime == null || startTime.after(endTime)) {
            throw new InvalidEntityException("Invalid schedule");
        }
        var ticketDocuments = StreamSupport.stream(ticketCollection.find(session, Filters.and(
                Filters.eq("race", race.id()), Filters.eq("place", place.id()))).spliterator(), false).toList();
        getTickets(session, ticketDocuments).forEach(found -> {
            if (startTime.before(found.end().arrival()) && endTime.after(found.start().departure())) {
                throw new PlaceAlreadyReservedException(ticket.race(), ticket.railcar(), ticket.place().number());
            }
        });
    }

//    private void checkIfCorrect(Connection connection, Ticket ticket) throws SQLException {
//        var railcarId = 0L;
//        var placeCost = BigDecimal.ZERO;
//        try (var statement = connection.prepareStatement(
//                "SELECT railcar_id, place_cost FROM places WHERE id = (?);"
//        )) {
//            statement.setLong(1, Long.parseLong(ticket.place().id().id()));
//            try (var resultSet = statement.executeQuery()) {
//                if (resultSet.next()) {
//                    railcarId = resultSet.getLong("railcar_id");
//                    placeCost = resultSet.getBigDecimal("place_cost");
//                } else {
//                    throw new InvalidEntityException("Place not found");
//                }
//            }
//        }
//        try (var  statement = connection.prepareStatement(
//                "WITH curr_train_id AS (SELECT train_id FROM races WHERE id = (?)) " +
//                        "SELECT * FROM railcars_in_trains WHERE train_id = (SELECT * FROM curr_train_id) LIMIT (?); "
//        )) {
//            statement.setLong(1, Long.parseLong(ticket.race().id()));
//            statement.setInt(2, ticket.railcar());
//            try (var resultSet = statement.executeQuery()) {
//                for (var i = 0; i < ticket.railcar(); ++i) {
//                    if (!resultSet.next()) {
//                        throw new InvalidEntityException("Railcar not found");
//                    }
//                }
//                if (resultSet.getLong("railcar_id") != railcarId) {
//                    throw new InvalidEntityException("Invalid railcar");
//                }
//            }
//        }
//        try (var  statement = connection.prepareStatement(
//                "SELECT * FROM schedule WHERE race_id = (?) AND id IN ((?), (?));"
//        )) {
//            statement.setLong(1, Long.parseLong(ticket.race().id()));
//            statement.setLong(2, Long.parseLong(ticket.start().id().id()));
//            statement.setLong(3, Long.parseLong(ticket.end().id().id()));
//            try (var resultSet = statement.executeQuery()) {
//                if (!resultSet.next()) {
//                    throw new InvalidEntityException("Departure not found");
//                }
//                var multiplier = BigDecimal.valueOf(resultSet.getDouble("multiplier"));
//                if (!resultSet.next()) {
//                    throw new InvalidEntityException("Destination not found");
//                }
//                multiplier = BigDecimal.valueOf(resultSet.getDouble("multiplier")).subtract(multiplier);
//                placeCost = placeCost.multiply(multiplier);
//                placeCost = placeCost.setScale(2, RoundingMode.HALF_UP);
//                if (placeCost.compareTo(ticket.cost().setScale(2, RoundingMode.HALF_UP)) != 0) {
//                    throw new InvalidEntityException("Invalid cost");
//                }
//            }
//        }
//    }

//    private void checkIfNotExists(Connection connection, Ticket ticket) throws SQLException {
//        try (var statement = connection.prepareStatement(
//                "SELECT * FROM races WHERE id = (?) AND finished = FALSE;"
//        )) {
//            statement.setLong(1, Long.parseLong(ticket.race().id()));
//            try (var resultSet = statement.executeQuery()) {
//                if (!resultSet.next()) {
//                    throw new InvalidEntityException("Race already finished");
//                }
//            }
//        }
//        var startTimestamp = (Timestamp) null;
//        var endTimestamp = (Timestamp) null;
//        try (var statement = connection.prepareStatement(
//                "SELECT arrival, departure FROM schedule WHERE id = (?) AND race_id = (?); "
//        )) {
//            statement.setLong(1, Long.parseLong(ticket.start().id().id()));
//            statement.setLong(2, Long.parseLong(ticket.race().id()));
//            try (var resultSet = statement.executeQuery()) {
//                if (!resultSet.next()) {
//                    throw new InvalidEntityException("Schedule not found");
//                }
//                startTimestamp = resultSet.getTimestamp("departure");
//            }
//            statement.setLong(1, Long.parseLong(ticket.end().id().id()));
//            statement.setLong(2, Long.parseLong(ticket.race().id()));
//            try (var resultSet = statement.executeQuery()) {
//                if (!resultSet.next()) {
//                    throw new InvalidEntityException("Schedule not found");
//                }
//                endTimestamp = resultSet.getTimestamp("arrival");
//            }
//        }
//        if (startTimestamp == null || endTimestamp == null || startTimestamp.after(endTimestamp)) {
//            throw new InvalidEntityException("Invalid schedule");
//        }
//        try (var statement = connection.prepareStatement(
//                "WITH race_tickets AS (SELECT s1.departure as start_time, s2.arrival as end_time " +
//                        "FROM tickets t JOIN schedule s1 ON t.departure = s1.id JOIN schedule s2 ON t.destination = s2.id " +
//                        "WHERE t.race_id = (?) AND t.place_id = (?)) " +
//                        "SELECT * FROM race_tickets WHERE " +
//                        "start_time >= (?) AND start_time < (?) OR end_time > (?) AND end_time <= (?); "
//        )) {
//            statement.setLong(1, Long.parseLong(ticket.race().id()));
//            statement.setLong(2, Long.parseLong(ticket.place().id().id()));
//            statement.setTimestamp(3, startTimestamp);
//            statement.setTimestamp(4, endTimestamp);
//            statement.setTimestamp(5, startTimestamp);
//            statement.setTimestamp(6, endTimestamp);
//            try (var resultSet = statement.executeQuery()) {
//                if (resultSet.next()) {
//                    throw new PlaceAlreadyReservedException(ticket.race(), ticket.railcar(), ticket.place().number());
//                }
//            }
//        }
//    }

    @Override
    public Iterable<Ticket> getTicketsByUser(UserId userId) {
        return mongoExecutor.transactionFunction(session -> {
            var objectUserId = new ObjectId(userId.id());
            var tickets = StreamSupport.stream(ticketCollection.find(session,
                    Filters.eq("owner", objectUserId)).spliterator(), false).toList();
            return getTickets(session, tickets);
        });
    }

//    @Override
//    public Iterable<Ticket> getTicketsByUser(UserId userId) {
//        return jdbcTemplate.executeFunc(Connection.TRANSACTION_REPEATABLE_READ, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM tickets WHERE user_id = (?);"
//            )) {
//                statement.setLong(1, Long.parseLong(userId.id()));
//                return getTickets(connection, statement);
//            }
//        });
//    }

    @Override
    public Iterable<Ticket> getTicketsByRace(RaceId raceId) {
        return mongoExecutor.transactionFunction(session -> {
            var objectRaceId = new ObjectId(raceId.id());
            var tickets = StreamSupport.stream(ticketCollection.find(session,
                    Filters.eq("race", objectRaceId)).spliterator(), false).toList();
            return getTickets(session, tickets);
        });
    }

    private List<Ticket> getTickets(ClientSession session, List<TicketDocument> ticketDocuments) {
        var schedules = new HashMap<ObjectId, Schedule>();
        var places = new HashMap<ObjectId, Place>();
        ticketDocuments.forEach(ticket -> {
            schedules.put(ticket.start(), null);
            schedules.put(ticket.end(), null);
            places.put(ticket.place(), null);
        });
        scheduleCollection.find(session, Filters.in("_id", schedules.keySet()))
                .forEach(scheduleDocument -> schedules.put(scheduleDocument.id(), scheduleDocument.toSchedule()));
        placeCollection.find(session, Filters.in("_id", places.keySet()))
                .forEach(placeDocument -> places.put(placeDocument.id(), placeDocument.toPlace()));
        return ticketDocuments.stream().map(ticketDocument -> {
            var place = places.get(ticketDocument.place());
            var start = schedules.get(ticketDocument.start());
            var end = schedules.get(ticketDocument.end());
            return ticketDocument.toTicket(place, start, end);
        }).toList();
    }

//    @Override
//    public Iterable<Ticket> getTicketsByRace(RaceId raceId) {
//        return jdbcTemplate.executeFunc(Connection.TRANSACTION_REPEATABLE_READ, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM tickets WHERE race_id = (?);"
//            )) {
//                statement.setLong(1, Long.parseLong(raceId.id()));
//                return getTickets(connection, statement);
//            }
//        });
//    }
//
//    private List<Ticket> getTickets(Connection connection, PreparedStatement statement) throws SQLException {
//        try (var resultSet = statement.executeQuery()) {
//            var tickets = new ArrayList<Ticket>();
//            var ticket = getTicket(connection, resultSet);
//            while (ticket != null) {
//                tickets.add(ticket);
//                ticket = getTicket(connection, resultSet);
//            }
//            return tickets;
//        }
//    }
//
//    private Ticket getTicket(Connection connection, ResultSet resultSet) throws SQLException {
//        var result = (Ticket) null;
//        if (resultSet.next()) {
//            var id = new TicketId(String.valueOf(resultSet.getLong("id")));
//            var owner = new UserId(String.valueOf(resultSet.getLong("user_id")));
//            var passenger = resultSet.getString("passenger");
//            var race = new RaceId(String.valueOf(resultSet.getLong("race_id")));
//            var railcar = resultSet.getInt("railcar");
//            var place = getPlace(connection, resultSet.getLong("place_id"));
//            var start = getSchedule(connection, resultSet.getLong("departure"));
//            var end = getSchedule(connection, resultSet.getLong("destination"));
//            var cost = resultSet.getBigDecimal("ticket_cost");
//            result = new Ticket(id, owner, passenger, race, railcar, place, start, end, cost);
//        }
//        return result;
//    }
//
//    private Place getPlace(Connection connection, long placeId) throws SQLException {
//        try (var statement = connection.prepareStatement(
//                "SELECT * FROM places WHERE id = (?);"
//        )) {
//            statement.setLong(1, placeId);
//            try (var resultSet = statement.executeQuery()) {
//                resultSet.next();
//                var id = new PlaceId(String.valueOf(resultSet.getLong("id")));
//                var number = resultSet.getInt("place_number");
//                var description = resultSet.getString("description");
//                var purpose = resultSet.getString("purpose");
//                var cost = resultSet.getBigDecimal("place_cost");
//                return new Place(id, number, description, purpose, cost);
//            }
//        }
//    }
//
//    private Schedule getSchedule(Connection connection, long scheduleId) throws SQLException {
//        try (var statement = connection.prepareStatement(
//                "SELECT * FROM schedule WHERE id = (?);"
//        )) {
//            statement.setLong(1, scheduleId);
//            try (var resultSet = statement.executeQuery()) {
//                resultSet.next();
//                var id = new ScheduleId(String.valueOf(resultSet.getLong("id")));
//                var name = resultSet.getString("station_name");
//                var arrival = resultSet.getTimestamp("arrival");
//                var departure = resultSet.getTimestamp("departure");
//                var multiplier = resultSet.getDouble("multiplier");
//                return new Schedule(id, name, arrival, departure, multiplier);
//            }
//        }
//    }
}
