package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;

import java.util.Optional;

public interface UserRepository {
    void addUser(String role, User user);
    Optional<User> getUser(String role, String username);
    Iterable<User> getUsers(String role, Iterable<UserId> userIds);
    void updateUser(String role, User user);
    void deleteUser(String role, UserId userId);
}
