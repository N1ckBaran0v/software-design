package traintickets.dataaccess.mongo.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import traintickets.businesslogic.model.Filter;
import traintickets.businesslogic.model.UserId;

import java.util.Map;

public record FilterDocument(@BsonId ObjectId id,
                             ObjectId user,
                             String name,
                             String departure,
                             String destination,
                             int transfers,
                             Map<String, Integer> passengers) {
    public FilterDocument(Filter filter) {
        this(
                null,
                new ObjectId(filter.user().id()),
                filter.name(),
                filter.departure(),
                filter.destination(),
                filter.transfers(),
                filter.passengers()
        );
    }

    public Filter toFilter() {
        return new Filter(
                new UserId(user.toHexString()),
                name,
                departure,
                destination,
                transfers,
                passengers,
                null,
                null
        );
    }
}
