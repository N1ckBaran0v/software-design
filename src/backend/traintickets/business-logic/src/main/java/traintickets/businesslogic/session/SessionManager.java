package traintickets.businesslogic.session;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.transport.UserInfo;

public interface SessionManager {
    void startSession(String sessionId, User user);
    UserInfo getUserInfo(String sessionId);
    void updateUserInfo(UserInfo userInfo);
    void endSession(String sessionId);
    void endSessions(UserId userId);
    String generateSessionId();
}
