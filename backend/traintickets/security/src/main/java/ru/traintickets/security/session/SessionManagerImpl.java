package ru.traintickets.security.session;

import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.model.UserId;
import ru.traintickets.businesslogic.session.SessionManager;
import ru.traintickets.businesslogic.transport.UserInfo;

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
