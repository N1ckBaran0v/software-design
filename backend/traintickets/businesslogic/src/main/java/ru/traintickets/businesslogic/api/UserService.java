package ru.traintickets.businesslogic.api;

import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.model.UserId;

public interface UserService {
    void createUser(User user);
    void deleteUser(UserId userId);
    void getUser(UserId userId);
    void getUserByAdmin(UserId userId);
    void updateUser(User user);
    void updateUserByAdmin(User user);
}
