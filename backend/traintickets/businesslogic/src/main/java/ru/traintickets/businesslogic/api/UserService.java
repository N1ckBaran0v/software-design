package ru.traintickets.businesslogic.api;

import ru.traintickets.businesslogic.exception.UserAlreadyExistsException;
import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.model.UserId;

public interface UserService {
    void createUser(User user) throws UserAlreadyExistsException;
    void deleteUser(UserId userId);
    User getUser(UserId userId);
    User getUserByAdmin(UserId userId);
    void updateUser(UserId userId, User user) throws UserAlreadyExistsException;
    void updateUserByAdmin(UserId userId, User user) throws UserAlreadyExistsException;
}
