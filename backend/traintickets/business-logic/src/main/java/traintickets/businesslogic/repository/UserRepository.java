package traintickets.businesslogic.repository;

import traintickets.businesslogic.exception.UserAlreadyExistsException;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;

import java.util.Optional;

public interface UserRepository {
    void addUser(User user) throws UserAlreadyExistsException;
    Optional<User> getUser(String username);
    Iterable<User> getUsers(Iterable<UserId> userIds);
    void updateUser(User user) throws UserAlreadyExistsException;
    void deleteUser(UserId userId);
}
