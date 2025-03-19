package traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.exception.UserAlreadyExistsException;
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
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionManager sessionManager;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_positive_created() throws UserAlreadyExistsException {
        var user = new User(null, "random_username", "qwerty123", "Zubenko Mikhail", "client", true);
        userService.createUser(user);
        verify(userRepository).addUser(user);
    }

    @Test
    void createUser_negative_exists() throws UserAlreadyExistsException {
        var username = "random_username";
        var user = new User(null, username, "qwerty123", "Zubenko Mikhail", "client", true);
        var exception = new UserAlreadyExistsException(username);
        willThrow(exception).given(userRepository).addUser(user);
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_negative_invalid() throws UserAlreadyExistsException {
        var user = new User(null, "random_username_long", "qwerty123", "Zubenko Mikhail", "client", true);
        assertThrows(InvalidEntityException.class, () -> userService.createUser(user));
        verify(userRepository, never()).addUser(any());
    }

    @Test
    void deleteUser_positive_deleted() {
        var userId = new UserId(1);
        userService.deleteUser(userId);
        verify(userRepository).deleteUser(userId);
        verify(sessionManager).endSessions(userId);
    }

    @Test
    void getUser_positive_found() {
        var username = "random_username";
        var user = new User(new UserId(1), username, "qwerty123", "Zubenko Mikhail", "client", true);
        given(userRepository.getUser(username)).willReturn(Optional.of(user));
        var result = userService.getUser(username);
        assertSame(user, result);
    }

    @Test
    void getUser_negative_notFound() {
        var username = "random_username";
        given(userRepository.getUser(username)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(username));
    }

    @Test
    void getUser_negative_banned() {
        var username = "random_username";
        var user = new User(new UserId(1), username, "qwerty123", "Zubenko Mikhail", "client", false);
        given(userRepository.getUser(username)).willReturn(Optional.of(user));
        assertThrows(UserWasBannedException.class, () -> userService.getUser(username));
    }

    @Test
    void getUserByAdmin_positive_found() {
        var username = "random_username";
        var user = new User(new UserId(1), username, "qwerty123", "Zubenko Mikhail", "client", true);
        given(userRepository.getUser(username)).willReturn(Optional.of(user));
        var result = userService.getUserByAdmin(username);
        assertSame(user, result);
    }

    @Test
    void getUserByAdmin_negative_notFound() {
        var username = "random_username";
        given(userRepository.getUser(username)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserByAdmin(username));
    }

    @Test
    void getUserByAdmin_positive_banned() {
        var username = "random_username";
        var user = new User(new UserId(1), username, "qwerty123", "Zubenko Mikhail", "client", false);
        given(userRepository.getUser(username)).willReturn(Optional.of(user));
        var result = userService.getUserByAdmin(username);
        assertSame(result, user);
    }

    @Test
    void updateUser_positive_updated() throws UserAlreadyExistsException {
        var userId = new UserId(1);
        var role = "client";
        var user = new User(userId, "random_username", "qwerty123", "Zubenko Mikhail", role, true);
        var uuid = UUID.randomUUID();
        var userInfo = new UserInfo(userId, role);
        given(sessionManager.getUserInfo(uuid)).willReturn(userInfo);
        userService.updateUser(uuid, user);
        verify(userRepository).updateUser(user);
    }

    @Test
    void updateUser_negative_invalid() throws UserAlreadyExistsException {
        var user = new User(new UserId(1), "random_username_long", "qwerty123", "Zubenko Mikhail", "client", true);
        assertThrows(InvalidEntityException.class, () -> userService.updateUser(UUID.randomUUID(), user));
        verify(sessionManager, never()).getUserInfo(any());
        verify(userRepository, never()).updateUser(any());
    }

    @Test
    void updateUser_negative_exists() throws UserAlreadyExistsException {
        var userId = new UserId(1);
        var username = "random_username";
        var role = "client";
        var user = new User(userId, username, "qwerty123", "Zubenko Mikhail", role, true);
        var uuid = UUID.randomUUID();
        var userInfo = new UserInfo(userId, role);
        given(sessionManager.getUserInfo(uuid)).willReturn(userInfo);
        var exception = new UserAlreadyExistsException(username);
        willThrow(exception).given(userRepository).updateUser(user);
        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(uuid, user));
    }

    @Test
    void updateUser_negative_banned() throws UserAlreadyExistsException {
        var user = new User(new UserId(1), "random_username", "qwerty123", "Zubenko Mikhail", "client", false);
        var uuid = UUID.randomUUID();
        assertThrows(InvalidEntityException.class, () -> userService.updateUser(uuid, user));
        verify(sessionManager, never()).getUserInfo(any());
        verify(userRepository, never()).updateUser(any());
    }

    @Test
    void updateUser_negative_changedRole() throws UserAlreadyExistsException {
        var userId = new UserId(1);
        var username = "random_username";
        var user = new User(userId, username, "qwerty123", "Zubenko Mikhail", "client", true);
        var uuid = UUID.randomUUID();
        var userInfo = new UserInfo(userId, "admin");
        given(sessionManager.getUserInfo(uuid)).willReturn(userInfo);
        assertThrows(InvalidEntityException.class, () -> userService.updateUser(uuid, user));
        verify(userRepository, never()).updateUser(any());
    }

    @Test
    void updateUserByAdmin_positive_updated() throws UserAlreadyExistsException {
        var user = new User(new UserId(1), "random_username", "qwerty123", "Zubenko Mikhail", "client", true);
        userService.updateUserByAdmin(user);
        verify(userRepository).updateUser(user);
    }

    @Test
    void updateUserByAdmin_negative_invalid() throws UserAlreadyExistsException {
        var user = new User(new UserId(1), "random_username_long", "qwerty123", "Zubenko Mikhail", "client", true);
        assertThrows(InvalidEntityException.class, () -> userService.updateUserByAdmin(user));
        verify(userRepository, never()).updateUser(any());
    }

    @Test
    void updateUserByAdmin_negative_exists() throws UserAlreadyExistsException {
        var username = "random_username";
        var user = new User(new UserId(1), username, "qwerty123", "Zubenko Mikhail", "client", true);
        var exception = new UserAlreadyExistsException(username);
        willThrow(exception).given(userRepository).updateUser(user);
        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUserByAdmin(user));
    }

    @Test
    void updateUserByAdmin_positive_banned() throws UserAlreadyExistsException {
        var user = new User(new UserId(1), "random_username", "qwerty123", "Zubenko Mikhail", "client", false);
        userService.updateUserByAdmin(user);
        verify(userRepository).updateUser(user);
    }
}