package traintickets.businesslogic.service;

import traintickets.businesslogic.api.AuthService;
import traintickets.businesslogic.exception.*;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.businesslogic.jwt.JwtManager;
import traintickets.businesslogic.transport.LoginForm;
import traintickets.businesslogic.transport.RegisterForm;
import traintickets.businesslogic.transport.UserInfo;

import java.util.Objects;

public final class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtManager jwtManager;
    private final String clientRole;
    private final String systemRole;

    public AuthServiceImpl(UserRepository userRepository,
                           JwtManager jwtManager,
                           String defaultRole,
                           String systemRole) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.jwtManager = Objects.requireNonNull(jwtManager);
        this.clientRole = Objects.requireNonNull(defaultRole);
        this.systemRole = Objects.requireNonNull(systemRole);
    }

    @Override
    public String register(RegisterForm form) {
        var username = form.username();
        var password = form.password();
        var confirmPassword = form.confirmPassword();
        if (password == null) {
            throw new InvalidEntityException("All data required");
        }
        if (!password.equals(confirmPassword)) {
            throw new PasswordsMismatchesException();
        }
        var name = form.name();
        var user = new User(null, username, password, name, clientRole, true);
        user.validate();
        return jwtManager.generateToken(UserInfo.of(userRepository.addUser(systemRole, user)));
    }

    @Override
    public String login(LoginForm form) {
        var user = userRepository.getUserByUsername(systemRole, form.username()).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", form.username())));
        if (!user.password().equals(form.password())) {
            throw new InvalidPasswordException();
        }
        if (!user.active()) {
            throw new UserWasBannedException(user.username());
        }
        return jwtManager.generateToken(UserInfo.of(user));
    }

    @Override
    public void logout(String token) {
        jwtManager.invalidateToken(token);
    }
}
