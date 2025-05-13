package traintickets.businesslogic.jwt;

import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.transport.UserInfo;

public interface JwtManager {
    String generateToken(UserId userId, String role);
    UserInfo validateToken(String token);
    void updateUser(UserId userId);
}
