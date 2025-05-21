package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.transport.TransportUser;

import java.util.Optional;

public interface UserRepository {
    User addUser(User user);
    Optional<User> getUserById(UserId userId);
    Optional<User> getUserByUsername(String username);
    Iterable<User> getUsers(Iterable<UserId> userIds);
    void updateUserCompletely(User user);
    void updateUserPartially(TransportUser user);
    void deleteUser(UserId userId);
}
