package traintickets.businesslogic.transport;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;

public record UserInfo(UserId userId, String role, String version) {
    public UserInfo(UserId userId, String role) {
        this(userId, role, null);
    }

    public static UserInfo of(User user) {
        return UserInfo.of(user, null);
    }

    public static UserInfo of(User user, String version) {
        return new UserInfo(user.id(), user.role(), version);
    }
}
