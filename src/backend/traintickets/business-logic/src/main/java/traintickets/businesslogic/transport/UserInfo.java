package traintickets.businesslogic.transport;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;

public record UserInfo(UserId userId, String role) {
    public static UserInfo of(User user) {
        return new UserInfo(user.id(), user.role());
    }
}
