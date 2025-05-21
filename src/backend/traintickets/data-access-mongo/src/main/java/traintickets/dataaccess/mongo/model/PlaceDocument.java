package traintickets.dataaccess.mongo.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import traintickets.businesslogic.model.Place;
import traintickets.businesslogic.model.PlaceId;

import java.math.BigDecimal;

public record PlaceDocument(@BsonId ObjectId id,
                            ObjectId railcarId,
                            int number,
                            String description,
                            String purpose,
                            BigDecimal cost) {
    public PlaceDocument(ObjectId railcarId, Place place) {
        this(
                place.id() == null ? null : new ObjectId(place.id().id()),
                railcarId,
                place.number(),
                place.description(),
                place.purpose(),
                place.cost()
        );
    }

    public Place toPlace() {
        return new Place(id == null ? null : new PlaceId(id.toHexString()), number, description, purpose, cost);
    }
}
