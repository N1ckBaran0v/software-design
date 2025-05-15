package traintickets.dataaccess.mongo.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;

import java.io.Serializable;

public record UserDocument(
        @BsonId ObjectId id,
        String username,
        String password,
        String name,
        String role,
        boolean active) implements Serializable {
    public UserDocument(User user) {
        this(
                user.id() == null ? null : new ObjectId(user.id().id()),
                user.username(),
                user.password(),
                user.name(),
                user.role(),
                user.active()
        );
    }

    public User toUser() {
        return new User(id == null ? null : new UserId(id.toHexString()), username, password, name, role, active);
    }
}
