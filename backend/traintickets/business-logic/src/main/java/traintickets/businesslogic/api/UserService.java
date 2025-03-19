package traintickets.businesslogic.api;

import traintickets.businesslogic.exception.UserAlreadyExistsException;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;

import java.util.UUID;

public interface UserService {
    void createUser(User user) throws UserAlreadyExistsException;
    void deleteUser(UserId userId);
    User getUser(String username);
    User getUserByAdmin(String username);
    void updateUser(UUID sessionId, User user) throws UserAlreadyExistsException;
    void updateUserByAdmin(User user) throws UserAlreadyExistsException;
}
