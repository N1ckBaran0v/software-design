package traintickets.dataaccess.mongo.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import traintickets.businesslogic.model.Place;
import traintickets.businesslogic.model.Railcar;
import traintickets.businesslogic.model.RailcarId;

import java.util.List;

public record RailcarDocument(@BsonId ObjectId id, String model, String type) {
    public RailcarDocument(Railcar railcar) {
        this(
                railcar.id() == null ? null : new ObjectId(railcar.id().id()),
                railcar.model(),
                railcar.type()
        );
    }

    public Railcar toRailcar(List<Place> places) {
        return new Railcar(id == null ? null : new RailcarId(id.toHexString()), model, type, places);
    }
}
