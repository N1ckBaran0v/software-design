package traintickets.businesslogic.session;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.transport.UserInfo;

import java.util.UUID;

public interface SessionManager {
    void startSession(UUID sessionId, User user);
    UserInfo getUserInfo(UUID sessionId);
    void updateUserInfo(UserInfo userInfo);
    void endSession(UUID sessionId);
    void endSessions(UserId userId);
    UUID generateSessionId();
}
