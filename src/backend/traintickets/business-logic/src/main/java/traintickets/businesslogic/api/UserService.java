package traintickets.businesslogic.api;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.transport.TransportUser;
import traintickets.businesslogic.transport.UserInfo;

public interface UserService {
    void createUser(User user);
    void deleteUser(UserId userId);
    TransportUser getUser(UserId userId);
    User getUserByAdmin(String username);
    void updateUser(UserInfo userInfo, TransportUser user);
    void updateUserByAdmin(User user);
}
