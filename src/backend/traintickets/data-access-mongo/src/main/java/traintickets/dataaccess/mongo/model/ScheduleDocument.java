package traintickets.dataaccess.mongo.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import traintickets.businesslogic.model.Schedule;
import traintickets.businesslogic.model.ScheduleId;

import java.util.Date;

public record ScheduleDocument(@BsonId ObjectId id,
                               ObjectId raceId,
                               String name,
                               Date arrival,
                               Date departure,
                               double multiplier) {
    public ScheduleDocument(ObjectId raceId, Schedule schedule) {
        this(
                schedule.id() == null ? null : new ObjectId(schedule.id().id()),
                raceId,
                schedule.name(),
                schedule.arrival(),
                schedule.departure(),
                schedule.multiplier()
        );
    }

    public Schedule toSchedule() {
        return new Schedule(id == null ? null : new ScheduleId(id.toHexString()), name, arrival, departure, multiplier);
    }
}
