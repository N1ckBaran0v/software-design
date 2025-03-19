package ru.traintickets.dataaccess.repository;

import ru.traintickets.businesslogic.exception.UserAlreadyExistsException;
import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.model.UserId;
import ru.traintickets.businesslogic.repository.UserRepository;

import java.util.Optional;

public final class UserRepositoryImpl implements UserRepository {
    @Override
    public void addUser(User user) throws UserAlreadyExistsException {
    }

    @Override
    public Optional<User> getUser(String username) {
        return Optional.empty();
    }

    @Override
    public Iterable<User> getUsers(Iterable<UserId> userIds) {
        return null;
    }

    @Override
    public void updateUser(User user) throws UserAlreadyExistsException {
    }

    @Override
    public void deleteUser(UserId userId) {
    }
}
