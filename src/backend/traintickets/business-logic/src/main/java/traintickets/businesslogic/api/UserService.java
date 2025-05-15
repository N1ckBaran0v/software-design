package traintickets.businesslogic.api;

import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.transport.TransportUser;
import traintickets.businesslogic.transport.UserInfo;

public interface UserService {
    void createUser(UserInfo userInfo, User user);
    void deleteUser(UserInfo userInfo, UserId userId);
    TransportUser getUser(UserInfo userInfo, UserId userId);
    User getUserByAdmin(UserInfo userInfo, String username);
    void updateUser(UserInfo userInfo, TransportUser user);
    void updateUserByAdmin(UserInfo userInfo, User user);
}
