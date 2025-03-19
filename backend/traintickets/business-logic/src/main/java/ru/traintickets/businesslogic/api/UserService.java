package ru.traintickets.businesslogic.api;

import ru.traintickets.businesslogic.exception.UserAlreadyExistsException;
import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.model.UserId;

public interface UserService {
    void createUser(User user) throws UserAlreadyExistsException;
    void deleteUser(UserId userId);
    User getUser(String username);
    User getUserByAdmin(String username);
    void updateUser(User user) throws UserAlreadyExistsException;
    void updateUserByAdmin(User user) throws UserAlreadyExistsException;
}
