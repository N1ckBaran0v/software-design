package traintickets.businesslogic.api;

import traintickets.businesslogic.transport.LoginForm;
import traintickets.businesslogic.transport.RegisterForm;

public interface AuthService {
    String register(RegisterForm form);
    String login(LoginForm form);
    void logout(String token);
}
