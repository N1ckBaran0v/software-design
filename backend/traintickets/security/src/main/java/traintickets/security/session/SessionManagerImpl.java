package traintickets.security.session;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.session.SessionManager;
import traintickets.businesslogic.transport.UserInfo;

import java.util.UUID;

public final class SessionManagerImpl implements SessionManager {
    @Override
    public void startSession(UUID sessionId, User user) {
    }

    @Override
    public UserInfo getUserInfo(UUID sessionId) {
        return null;
    }

    @Override
    public void updateUserInfo(UserInfo userInfo) {
    }

    @Override
    public void endSession(UUID sessionId) {
    }

    @Override
    public void endSessions(UserId userId) {
    }
}
