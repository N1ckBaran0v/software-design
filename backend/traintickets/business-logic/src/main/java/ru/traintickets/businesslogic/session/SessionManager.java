package ru.traintickets.businesslogic.session;

import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.model.UserId;
import ru.traintickets.businesslogic.transport.UserInfo;

import java.util.UUID;

public interface SessionManager {
    void startSession(UUID sessionId, User user);
    UserInfo getUserInfo(UUID sessionId);
    void updateUserInfo(UserInfo userInfo);
    void endSession(UUID sessionId);
    void endSessions(UserId userId);
}
