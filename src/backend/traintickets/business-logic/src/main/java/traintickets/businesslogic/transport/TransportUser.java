package traintickets.businesslogic.transport;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;

import java.io.Serializable;

public record TransportUser(UserId id, String username, String password, String name) implements Serializable {
    public static TransportUser from(User user) {
        return new TransportUser(user.id(), user.username(), user.password(), user.name());
    }

    public User toUser(String role, boolean isActive) {
        return new User(id, username, password, name, role, isActive);
    }
}
