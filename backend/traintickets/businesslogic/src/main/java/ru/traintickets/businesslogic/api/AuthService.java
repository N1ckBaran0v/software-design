package ru.traintickets.businesslogic.api;

import ru.traintickets.businesslogic.exception.UserAlreadyExistsException;
import ru.traintickets.businesslogic.transport.LoginForm;
import ru.traintickets.businesslogic.transport.RegisterForm;

import java.util.UUID;

public interface AuthService {
    void register(UUID sessionId, RegisterForm form) throws UserAlreadyExistsException;
    void login(UUID sessionId, LoginForm form);
    void logout(UUID sessionId);
}
