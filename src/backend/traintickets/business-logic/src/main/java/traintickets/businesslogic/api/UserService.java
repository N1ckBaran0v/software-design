package traintickets.businesslogic.api;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.transport.TransportUser;

import java.util.UUID;

public interface UserService {
    void createUser(User user);
    void deleteUser(UserId userId);
    TransportUser getUser(String username);
    User getUserByAdmin(String username);
    void updateUser(String sessionId, TransportUser user);
    void updateUserByAdmin(User user);
}
