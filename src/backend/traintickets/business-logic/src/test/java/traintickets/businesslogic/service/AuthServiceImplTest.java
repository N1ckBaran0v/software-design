package traintickets.businesslogic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.*;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.businesslogic.session.SessionManager;
import traintickets.businesslogic.transport.LoginForm;
import traintickets.businesslogic.transport.RegisterForm;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionManager sessionManager;

    private final String clientRole = "client_role";
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, sessionManager, clientRole);
    }

    @Test
    void register_positive_saved() {
        var form = new RegisterForm("random_username", "qwerty123", "qwerty123", "Zubenko Mikhail");
        authService.register(UUID.randomUUID(), form);
        verify(userRepository).addUser(any());
        verify(sessionManager).startSession(any(), any());
    }

    @Test
    void register_negative_empty() {
        var form = new RegisterForm("random_username", null, "qwerty123", "Zubenko Mikhail");
        assertThrows(InvalidEntityException.class, () -> authService.register(UUID.randomUUID(), form));
        verify(userRepository, never()).addUser(any());
        verify(sessionManager, never()).startSession(any(), any());
    }

    @Test
    void register_negative_invalid() {
        var form = new RegisterForm("random_username_long", "qwerty123", "qwerty123", "Zubenko Mikhail");
        assertThrows(InvalidEntityException.class, () -> authService.register(UUID.randomUUID(), form));
        verify(userRepository, never()).addUser(any());
        verify(sessionManager, never()).startSession(any(), any());
    }

    @Test
    void register_negative_mismatches() {
        var form = new RegisterForm("random_username", "qwerty123", "qwertu123", "Zubenko Mikhail");
        assertThrows(PasswordsMismatchesException.class, () -> authService.register(UUID.randomUUID(), form));
        verify(userRepository, never()).addUser(any());
        verify(sessionManager, never()).startSession(any(), any());
    }

    @Test
    void login_positive_loggedIn() {
        var username = "random_username";
        var password = "qwerty123";
        var form = new LoginForm(username, password);
        var user = new User(new UserId(1), username, password, "Zubenko Mikhail", clientRole, true);
        var sessionId = UUID.randomUUID();
        given(userRepository.getUser(username)).willReturn(Optional.of(user));
        authService.login(sessionId, form);
        verify(sessionManager).startSession(sessionId, user);
    }

    @Test
    void login_negative_notFound() {
        var username = "random_username";
        var password = "qwerty123";
        var form = new LoginForm(username, password);
        var sessionId = UUID.randomUUID();
        given(userRepository.getUser(username)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> authService.login(sessionId, form));
        verify(sessionManager, never()).startSession(any(), any());
    }

    @Test
    void login_negative_invalidPassword() {
        var username = "random_username";
        var form = new LoginForm(username, "qwerty1234");
        var sessionId = UUID.randomUUID();
        var user = new User(null, username, "qwerty123", "Zubenko Mikhail", clientRole, true);
        given(userRepository.getUser(username)).willReturn(Optional.of(user));
        assertThrows(InvalidPasswordException.class, () -> authService.login(sessionId, form));
        verify(sessionManager, never()).startSession(any(), any());
    }

    @Test
    void login_negative_banned() {
        var username = "random_username";
        var password = "qwerty123";
        var form = new LoginForm(username, password);
        var sessionId = UUID.randomUUID();
        var user = new User(null, username, password, "Zubenko Mikhail", clientRole, false);
        given(userRepository.getUser(username)).willReturn(Optional.of(user));
        assertThrows(UserWasBannedException.class, () -> authService.login(sessionId, form));
        verify(sessionManager, never()).startSession(any(), any());
    }

    @Test
    void logout_positive_loggedOut() {
        var sessionId = UUID.randomUUID();
        authService.logout(sessionId);
        verify(sessionManager).endSession(sessionId);
    }
}