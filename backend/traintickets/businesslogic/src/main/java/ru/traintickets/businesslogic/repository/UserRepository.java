package ru.traintickets.businesslogic.repository;

import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.model.UserId;

import java.util.Optional;

public interface UserRepository {
    void addUser(User user);
    Optional<User> getUser(UserId userId);
    void updateUser(User user);
    void deleteUser(UserId userId);
}
