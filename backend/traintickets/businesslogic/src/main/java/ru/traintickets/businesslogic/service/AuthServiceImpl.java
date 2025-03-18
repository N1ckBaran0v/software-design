package ru.traintickets.businesslogic.service;

import ru.traintickets.businesslogic.api.AuthService;
import ru.traintickets.businesslogic.exception.EntityNotFoundException;
import ru.traintickets.businesslogic.exception.InvalidPasswordException;
import ru.traintickets.businesslogic.exception.UserAlreadyExistsException;
import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.repository.UserRepository;
import ru.traintickets.businesslogic.session.SessionManager;
import ru.traintickets.businesslogic.transport.LoginForm;
import ru.traintickets.businesslogic.transport.RegisterForm;

import java.util.Objects;
import java.util.UUID;

public final class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public AuthServiceImpl(UserRepository userRepository, SessionManager sessionManager) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.sessionManager = Objects.requireNonNull(sessionManager);
    }

    @Override
    public void register(UUID sessionId, RegisterForm form) throws UserAlreadyExistsException {
        var username = form.username();
        var password = form.password();
        var name = form.name();
        var role = "client_user";
        var user = new User(username, password, name, role, false);
        userRepository.addUser(user);
        sessionManager.startSession(sessionId, user);
    }

    @Override
    public void login(UUID sessionId, LoginForm form) {
        var user = userRepository.getUser(form.username()).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", form.username())));
        if (!user.password().equals(form.password())) {
            throw new InvalidPasswordException();
        }
        sessionManager.startSession(sessionId, user);
    }

    @Override
    public void logout(UUID sessionId) {
        sessionManager.endSession(sessionId);
    }
}
