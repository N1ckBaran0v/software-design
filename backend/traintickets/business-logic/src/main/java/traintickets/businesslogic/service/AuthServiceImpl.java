package traintickets.businesslogic.service;

import traintickets.businesslogic.api.AuthService;
import traintickets.businesslogic.exception.*;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.businesslogic.session.SessionManager;
import traintickets.businesslogic.transport.LoginForm;
import traintickets.businesslogic.transport.RegisterForm;

import java.util.Objects;
import java.util.UUID;

public final class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final String clientRole;

    public AuthServiceImpl(UserRepository userRepository, SessionManager sessionManager, String clientRole) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.clientRole = Objects.requireNonNull(clientRole);
    }

    @Override
    public void register(UUID sessionId, RegisterForm form) {
        var username = form.username();
        var password = form.password();
        var confirmPassword = form.confirmPassword();
        if (!password.equals(confirmPassword)) {
            throw new PasswordsMismatchesException();
        }
        var name = form.name();
        var user = new User(null, username, password, name, clientRole, true);
        user.validate();
        userRepository.addUser(user);
        sessionManager.startSession(sessionId, user);
    }

    @Override
    public void login(UUID sessionId, LoginForm form) {
        var user = userRepository.getUser(form.username()).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", form.username())));
        if (!user.password().equals(form.password())) {
            throw new InvalidPasswordException();
        }
        if (!user.active()) {
            throw new UserWasBannedException(user.username());
        }
        sessionManager.startSession(sessionId, user);
    }

    @Override
    public void logout(UUID sessionId) {
        sessionManager.endSession(sessionId);
    }
}
