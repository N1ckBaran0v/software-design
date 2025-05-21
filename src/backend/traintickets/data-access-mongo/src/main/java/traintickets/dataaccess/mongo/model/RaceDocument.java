package traintickets.dataaccess.mongo.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import traintickets.businesslogic.model.Race;
import traintickets.businesslogic.model.RaceId;
import traintickets.businesslogic.model.Schedule;
import traintickets.businesslogic.model.TrainId;

import java.util.List;

public record RaceDocument(@BsonId ObjectId id, ObjectId trainId, boolean finished) {
    public RaceDocument(Race race) {
        this(
                race.id() == null ? null : new ObjectId(race.id().id()),
                new ObjectId(race.trainId().id()),
                race.finished()
        );
    }

    public Race toRace(List<Schedule> schedule) {
        return new Race(
                id == null ? null : new RaceId(id.toHexString()),
                new TrainId(trainId.toHexString()),
                schedule,
                finished
        );
    }
}
