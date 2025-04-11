package traintickets.businesslogic.api;

import traintickets.businesslogic.transport.LoginForm;
import traintickets.businesslogic.transport.RegisterForm;

public interface AuthService {
    void register(String sessionId, RegisterForm form);
    void login(String sessionId, LoginForm form);
    void logout(String sessionId);
}
