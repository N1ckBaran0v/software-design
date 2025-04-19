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
import traintickets.businesslogic.session.JwtManager;
import traintickets.businesslogic.transport.LoginForm;
import traintickets.businesslogic.transport.RegisterForm;

import java.util.Optional;

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
    private JwtManager jwtManager;

    private final String clientRole = "client_role";
    private final String systemRole = "system_role";
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, jwtManager, clientRole, systemRole);
    }

    @Test
    void register_positive_saved() {
        var form = new RegisterForm("random_username", "qwerty123", "qwerty123", "Zubenko Mikhail");
        given(userRepository.addUser(any(), any())).willReturn(new User(new UserId("1"), "random_username", "qwerty123", "Zubenko Mikhail", "user_role", true));
        var token = "random_maybe_invalid_jwt_token";
        given(jwtManager.generateToken(any())).willReturn(token);
        var result = authService.register(form);
        assertNotNull(result);
        assertEquals(token, result);
    }

    @Test
    void register_negative_empty() {
        var form = new RegisterForm("random_username", null, "qwerty123", "Zubenko Mikhail");
        assertThrows(InvalidEntityException.class, () -> authService.register(form));
        verify(userRepository, never()).addUser(any(), any());
        verify(jwtManager, never()).generateToken(any());
    }

    @Test
    void register_negative_invalid() {
        var form = new RegisterForm("random_username_long", "qwerty123", "qwerty123", "Zubenko Mikhail");
        assertThrows(InvalidEntityException.class, () -> authService.register(form));
        verify(userRepository, never()).addUser(any(), any());
        verify(jwtManager, never()).generateToken(any());
    }

    @Test
    void register_negative_mismatches() {
        var form = new RegisterForm("random_username", "qwerty123", "qwertu123", "Zubenko Mikhail");
        assertThrows(PasswordsMismatchesException.class, () -> authService.register(form));
        verify(userRepository, never()).addUser(any(), any());
        verify(jwtManager, never()).generateToken(any());
    }

    @Test
    void login_positive_loggedIn() {
        var username = "random_username";
        var password = "qwerty123";
        var form = new LoginForm(username, password);
        var user = new User(new UserId("1"), username, password, "Zubenko Mikhail", clientRole, true);
        given(userRepository.getUser(systemRole, username)).willReturn(Optional.of(user));
        var token = "random_maybe_invalid_jwt_token";
        given(jwtManager.generateToken(any())).willReturn(token);
        var result = authService.login(form);
        assertNotNull(result);
        assertEquals(token, result);
    }

    @Test
    void login_negative_notFound() {
        var username = "random_username";
        var password = "qwerty123";
        var form = new LoginForm(username, password);
        given(userRepository.getUser(systemRole, username)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> authService.login(form));
        verify(jwtManager, never()).generateToken(any());
    }

    @Test
    void login_negative_invalidPassword() {
        var username = "random_username";
        var form = new LoginForm(username, "qwerty1234");
        var user = new User(null, username, "qwerty123", "Zubenko Mikhail", clientRole, true);
        given(userRepository.getUser(systemRole, username)).willReturn(Optional.of(user));
        assertThrows(InvalidPasswordException.class, () -> authService.login(form));
        verify(jwtManager, never()).generateToken(any());
    }

    @Test
    void login_negative_banned() {
        var username = "random_username";
        var password = "qwerty123";
        var form = new LoginForm(username, password);
        var user = new User(null, username, password, "Zubenko Mikhail", clientRole, false);
        given(userRepository.getUser(systemRole, username)).willReturn(Optional.of(user));
        assertThrows(UserWasBannedException.class, () -> authService.login(form));
        verify(jwtManager, never()).generateToken(any());
    }

    @Test
    void logout_positive_loggedOut() {
        var token = "random_maybe_invalid_jwt_token";
        authService.logout(token);
        verify(jwtManager).invalidateToken(token);
    }
}