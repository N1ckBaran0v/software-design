package traintickets.dataaccess.mongo.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import traintickets.businesslogic.model.RailcarId;
import traintickets.businesslogic.model.Train;
import traintickets.businesslogic.model.TrainId;

import java.util.List;

public record TrainDocument(@BsonId ObjectId id, String trainClass, List<ObjectId> railcars) {
    public TrainDocument(Train train) {
        this(train.id() == null ? null : new ObjectId(train.id().id()), train.trainClass(),
                train.railcars().stream().map(railcarId -> new ObjectId(railcarId.id())).toList());
    }

    public Train toTrain() {
        return new Train(id == null ? null : new TrainId(id.toHexString()), trainClass,
                railcars.stream().map(railcarId -> new RailcarId(railcarId.toHexString())).toList());
    }
}
