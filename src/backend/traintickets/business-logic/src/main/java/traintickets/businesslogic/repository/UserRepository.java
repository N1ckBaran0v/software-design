package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.transport.TransportUser;

import java.util.Optional;

public interface UserRepository {
    User addUser(String role, User user);
    Optional<User> getUserById(String role, UserId userId);
    Optional<User> getUserByUsername(String role, String username);
    Iterable<User> getUsers(String role, Iterable<UserId> userIds);
    void updateUserCompletely(String role, User user);
    void updateUserPartially(String role, TransportUser user);
    void deleteUser(String role, UserId userId);
}
