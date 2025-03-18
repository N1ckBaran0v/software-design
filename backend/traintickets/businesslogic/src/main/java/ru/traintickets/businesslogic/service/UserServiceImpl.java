package ru.traintickets.businesslogic.service;

import ru.traintickets.businesslogic.api.UserService;
import ru.traintickets.businesslogic.exception.EntityNotFoundException;
import ru.traintickets.businesslogic.exception.UserAlreadyExistsException;
import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.model.UserId;
import ru.traintickets.businesslogic.repository.UserRepository;
import ru.traintickets.businesslogic.session.SessionManager;

import java.util.Objects;

public final class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public UserServiceImpl(UserRepository userRepository, SessionManager sessionManager) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.sessionManager = Objects.requireNonNull(sessionManager);
    }

    @Override
    public void createUser(User user) throws UserAlreadyExistsException {
        user.validate();
        userRepository.addUser(user);
    }

    @Override
    public void deleteUser(UserId userId) {
        userRepository.deleteUser(userId);
        sessionManager.endSessions(userId);
    }

    @Override
    public User getUser(UserId userId) {
        return userRepository.getUser(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with username '%s' not found", userId.id())));
    }

    @Override
    public User getUserByAdmin(UserId userId) {
        return userRepository.getUser(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with username '%s' not found", userId.id())));
    }

    @Override
    public void updateUser(UserId userId, User user) throws UserAlreadyExistsException {
        user.validate();
        userRepository.updateUser(userId, user);
    }

    @Override
    public void updateUserByAdmin(UserId userId, User user) throws UserAlreadyExistsException {
        user.validate();
        userRepository.updateUser(userId, user);
    }
}
