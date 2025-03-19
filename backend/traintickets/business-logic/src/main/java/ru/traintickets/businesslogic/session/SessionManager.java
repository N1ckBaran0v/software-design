package ru.traintickets.businesslogic.session;

import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.model.UserId;

import java.util.UUID;

public interface SessionManager {
    void startSession(UUID sessionId, User user);
    void endSession(UUID sessionId);
    void endSessions(UserId userId);
}
