package traintickets.businesslogic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.exception.UserWasBannedException;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.businesslogic.session.SessionManager;
import traintickets.businesslogic.transport.UserInfo;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionManager sessionManager;

    private final String systemRole = "system_role";
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, sessionManager, systemRole);
    }

    @Test
    void createUser_positive_created() {
        var user = new User(null, "random_username", "qwerty123", "Zubenko Mikhail", "client", true);
        userService.createUser(user);
        verify(userRepository).addUser(systemRole, user);
    }

    @Test
    void createUser_negative_invalid() {
        var user = new User(null, "random_username_long", "qwerty123", "Zubenko Mikhail", "client", true);
        assertThrows(InvalidEntityException.class, () -> userService.createUser(user));
        verify(userRepository, never()).addUser(any(), any());
    }

    @Test
    void deleteUser_positive_deleted() {
        var userId = new UserId(1);
        userService.deleteUser(userId);
        verify(userRepository).deleteUser(systemRole, userId);
        verify(sessionManager).endSessions(userId);
    }

    @Test
    void getUser_positive_found() {
        var username = "random_username";
        var user = new User(new UserId(1), username, "qwerty123", "Zubenko Mikhail", "client", true);
        given(userRepository.getUser(systemRole, username)).willReturn(Optional.of(user));
        var result = userService.getUser(username);
        assertSame(user, result);
    }

    @Test
    void getUser_negative_notFound() {
        var username = "random_username";
        given(userRepository.getUser(systemRole, username)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(username));
    }

    @Test
    void getUser_negative_banned() {
        var username = "random_username";
        var user = new User(new UserId(1), username, "qwerty123", "Zubenko Mikhail", "client", false);
        given(userRepository.getUser(systemRole, username)).willReturn(Optional.of(user));
        assertThrows(UserWasBannedException.class, () -> userService.getUser(username));
    }

    @Test
    void getUserByAdmin_positive_found() {
        var username = "random_username";
        var user = new User(new UserId(1), username, "qwerty123", "Zubenko Mikhail", "client", true);
        given(userRepository.getUser(systemRole, username)).willReturn(Optional.of(user));
        var result = userService.getUserByAdmin(username);
        assertSame(user, result);
    }

    @Test
    void getUserByAdmin_negative_notFound() {
        var username = "random_username";
        given(userRepository.getUser(systemRole, username)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserByAdmin(username));
    }

    @Test
    void getUserByAdmin_positive_banned() {
        var username = "random_username";
        var user = new User(new UserId(1), username, "qwerty123", "Zubenko Mikhail", "client", false);
        given(userRepository.getUser(systemRole, username)).willReturn(Optional.of(user));
        var result = userService.getUserByAdmin(username);
        assertSame(result, user);
    }

    @Test
    void updateUser_positive_updated() {
        var userId = new UserId(1);
        var role = "client";
        var user = new User(userId, "random_username", "qwerty123", "Zubenko Mikhail", role, true);
        var uuid = UUID.randomUUID();
        var userInfo = new UserInfo(userId, role);
        given(sessionManager.getUserInfo(uuid)).willReturn(userInfo);
        userService.updateUser(uuid, user);
        verify(userRepository).updateUser(systemRole, user);
    }

    @Test
    void updateUser_negative_invalid() {
        var user = new User(new UserId(1), "random_username_long", "qwerty123", "Zubenko Mikhail", "client", true);
        assertThrows(InvalidEntityException.class, () -> userService.updateUser(UUID.randomUUID(), user));
        verify(sessionManager, never()).getUserInfo(any());
        verify(userRepository, never()).updateUser(any(), any());
    }

    @Test
    void updateUser_negative_banned() {
        var user = new User(new UserId(1), "random_username", "qwerty123", "Zubenko Mikhail", "client", false);
        var uuid = UUID.randomUUID();
        assertThrows(InvalidEntityException.class, () -> userService.updateUser(uuid, user));
        verify(sessionManager, never()).getUserInfo(any());
        verify(userRepository, never()).updateUser(any(), any());
    }

    @Test
    void updateUser_negative_changedRole() {
        var userId = new UserId(1);
        var username = "random_username";
        var user = new User(userId, username, "qwerty123", "Zubenko Mikhail", "admin", true);
        var uuid = UUID.randomUUID();
        var userInfo = new UserInfo(userId, "client");
        given(sessionManager.getUserInfo(uuid)).willReturn(userInfo);
        assertThrows(InvalidEntityException.class, () -> userService.updateUser(uuid, user));
        verify(userRepository, never()).updateUser(any(), any());
    }

    @Test
    void updateUserByAdmin_positive_updated() {
        var user = new User(new UserId(1), "random_username", "qwerty123", "Zubenko Mikhail", "admin", true);
        userService.updateUserByAdmin(user);
        verify(userRepository).updateUser(systemRole, user);
        verify(sessionManager).updateUserInfo(any());
    }

    @Test
    void updateUserByAdmin_negative_invalid() {
        var user = new User(new UserId(1), "random_username_long", "qwerty123", "Zubenko Mikhail", "client", true);
        assertThrows(InvalidEntityException.class, () -> userService.updateUserByAdmin(user));
        verify(userRepository, never()).updateUser(any(), any());
        verify(sessionManager, never()).updateUserInfo(any());
    }

    @Test
    void updateUserByAdmin_positive_banned() {
        var user = new User(new UserId(1), "random_username", "qwerty123", "Zubenko Mikhail", "client", false);
        userService.updateUserByAdmin(user);
        verify(userRepository).updateUser(systemRole, user);
        verify(sessionManager).updateUserInfo(any());
    }
}