package ru.traintickets.businesslogic.repository;

import ru.traintickets.businesslogic.exception.UserAlreadyExistsException;
import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.model.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void addUser(User user) throws UserAlreadyExistsException;
    Optional<User> getUser(UserId userId);
    Iterable<User> getUsers(List<UserId> userIds);
    void updateUser(UserId userId, User user) throws UserAlreadyExistsException;
    void deleteUser(UserId userId);
}
