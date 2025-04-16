package traintickets.businesslogic.session;

import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.transport.UserInfo;

public interface JwtManager {
    String generateToken(UserInfo userInfo);
    UserInfo validateToken(String token);
    void invalidateToken(String token);
    void invalidateTokens(UserId userId);
}
