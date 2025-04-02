package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;

import java.util.Optional;

public interface UserRepository {
    void addUser(User user);
    Optional<User> getUser(String username);
    Iterable<User> getUsers(Iterable<UserId> userIds);
    void updateUser(User user);
    void deleteUser(UserId userId);
}
