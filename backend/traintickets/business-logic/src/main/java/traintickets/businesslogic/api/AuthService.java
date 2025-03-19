package traintickets.businesslogic.api;

import traintickets.businesslogic.exception.UserAlreadyExistsException;
import traintickets.businesslogic.transport.LoginForm;
import traintickets.businesslogic.transport.RegisterForm;

import java.util.UUID;

public interface AuthService {
    void register(UUID sessionId, RegisterForm form) throws UserAlreadyExistsException;
    void login(UUID sessionId, LoginForm form);
    void logout(UUID sessionId);
}
