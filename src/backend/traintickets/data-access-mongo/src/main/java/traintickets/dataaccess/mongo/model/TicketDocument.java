package traintickets.dataaccess.mongo.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import traintickets.businesslogic.model.*;

import java.math.BigDecimal;

public record TicketDocument(@BsonId ObjectId ticketId,
                             ObjectId owner,
                             String passenger,
                             ObjectId race,
                             int railcar,
                             ObjectId place,
                             ObjectId start,
                             ObjectId end,
                             BigDecimal cost) {
    public TicketDocument(Ticket ticket) {
        this(
                ticket.ticketId() == null ? null : new ObjectId(ticket.ticketId().id()),
                new ObjectId(ticket.owner().id()),
                ticket.passenger(),
                new ObjectId(ticket.race().id()),
                ticket.railcar(),
                new ObjectId(ticket.place().id().id()),
                new ObjectId(ticket.start().id().id()),
                new ObjectId(ticket.end().id().id()),
                ticket.cost()
        );
    }

    public Ticket toTicket(Place place, Schedule startSchedule, Schedule endSchedule) {
        return new Ticket(
                ticketId == null ? null : new TicketId(ticketId.toHexString()),
                new UserId(owner.toHexString()),
                passenger,
                new RaceId(race.toHexString()),
                railcar,
                place,
                startSchedule,
                endSchedule,
                cost
        );
    }
}
