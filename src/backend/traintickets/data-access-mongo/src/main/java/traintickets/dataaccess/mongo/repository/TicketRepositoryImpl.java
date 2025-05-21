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

    private void checkIfCorrect(ClientSession session, Ticket ticket) {
        var place = placeCollection.find(session, Filters.eq("_id", new ObjectId(ticket.place().id().id()))).first();
        if (place == null) {
            throw new InvalidEntityException("Place not found");
        }
        var railcarId = place.railcarId();
        var placeCost = place.cost();
        var race = raceCollection.find(session, Filters.and(
                Filters.eq("_id", new ObjectId(ticket.race().id())), Filters.eq("finished", false)
        )).first();
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
            if (!(startTime.after(found.end().arrival()) || endTime.before(found.start().departure()))) {
                throw new PlaceAlreadyReservedException(ticket.race(), ticket.railcar(), ticket.place().number());
            }
        });
    }

    @Override
    public Iterable<Ticket> getTicketsByUser(UserId userId) {
        return mongoExecutor.transactionFunction(session -> {
            var objectUserId = new ObjectId(userId.id());
            var tickets = StreamSupport.stream(ticketCollection.find(session,
                    Filters.eq("owner", objectUserId)).spliterator(), false).toList();
            return getTickets(session, tickets);
        });
    }

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
}
