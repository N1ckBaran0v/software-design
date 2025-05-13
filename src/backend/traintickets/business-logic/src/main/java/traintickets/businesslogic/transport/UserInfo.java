package traintickets.businesslogic.transport;

import traintickets.businesslogic.model.UserId;

public record UserInfo(UserId userId, String role, String version) {
    public UserInfo(UserId userId, String role) {
        this(userId, role, null);
    }
}
