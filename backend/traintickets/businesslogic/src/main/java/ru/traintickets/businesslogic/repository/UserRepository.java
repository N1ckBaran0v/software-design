package ru.traintickets.businesslogic.repository;

import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.model.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void addUser(User user);
    Optional<User> getUser(UserId userId);
    Iterable<User> getUsers(List<UserId> userIds);
    void updateUser(User user);
    void deleteUser(UserId userId);
}
