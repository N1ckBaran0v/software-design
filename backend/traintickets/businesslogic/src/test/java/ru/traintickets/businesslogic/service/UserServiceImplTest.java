package ru.traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.traintickets.businesslogic.exception.EntityNotFoundException;
import ru.traintickets.businesslogic.exception.InvalidEntityException;
import ru.traintickets.businesslogic.exception.UserAlreadyExistsException;
import ru.traintickets.businesslogic.model.User;
import ru.traintickets.businesslogic.model.UserId;
import ru.traintickets.businesslogic.repository.UserRepository;
import ru.traintickets.businesslogic.session.SessionManager;

import java.util.Optional;

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
        var user = new User(new UserId("random_username"), "qwerty123", "Zubenko Mikhail", "client", true);
        userService.createUser(user);
        verify(userRepository).addUser(user);
    }

    @Test
    void createUser_negative_exists() throws UserAlreadyExistsException {
        var userId = new UserId("random_username");
        var user = new User(userId, "qwerty123", "Zubenko Mikhail", "client", true);
        var exception = new UserAlreadyExistsException(userId);
        willThrow(exception).given(userRepository).addUser(user);
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_negative_invalid() throws UserAlreadyExistsException {
        var user = new User(new UserId("random_username_long"), "qwerty123", "Zubenko Mikhail", "client", true);
        assertThrows(InvalidEntityException.class, () -> userService.createUser(user));
        verify(userRepository, never()).addUser(any());
    }

    @Test
    void deleteUser_positive_deleted() {
        var userId = new UserId("random_username");
        userService.deleteUser(userId);
        verify(userRepository).deleteUser(userId);
        verify(sessionManager).endSessions(userId);
    }

    @Test
    void getUser_positive_found() {
        var userId = new UserId("random_username");
        var user = new User(userId, "qwerty123", "Zubenko Mikhail", "client", true);
        given(userRepository.getUser(userId)).willReturn(Optional.of(user));
        var result = userService.getUser(userId);
        assertSame(user, result);
    }

    @Test
    void getUser_negative_notFound() {
        var userId = new UserId("random_username");
        given(userRepository.getUser(userId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void getUserByAdmin_positive_found() {
        var userId = new UserId("random_username");
        var user = new User(userId, "qwerty123", "Zubenko Mikhail", "client", true);
        given(userRepository.getUser(userId)).willReturn(Optional.of(user));
        var result = userService.getUserByAdmin(userId);
        assertSame(user, result);
    }

    @Test
    void getUserByAdmin_negative_notFound() {
        var userId = new UserId("random_username");
        given(userRepository.getUser(userId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserByAdmin(userId));
    }

    @Test
    void updateUser_positive_updated() throws UserAlreadyExistsException {
        var userId = new UserId("random_username");
        var user = new User(userId, "qwerty123", "Zubenko Mikhail", "client", true);
        userService.updateUser(userId, user);
        verify(userRepository).updateUser(userId, user);
    }

    @Test
    void updateUser_negative_invalid() throws UserAlreadyExistsException {
        var userId = new UserId("random_username");
        var user = new User(new UserId("random_username_long"), "qwerty123", "Zubenko Mikhail", "client", true);
        assertThrows(InvalidEntityException.class, () -> userService.updateUser(userId, user));
        verify(userRepository, never()).updateUser(any(), any());
    }

    @Test
    void updateUser_negative_exists() throws UserAlreadyExistsException {
        var userId = new UserId("random_username");
        var newUserId = new UserId("username_exists");
        var user = new User(userId, "qwerty123", "Zubenko Mikhail", "client", true);
        var exception = new UserAlreadyExistsException(newUserId);
        willThrow(exception).given(userRepository).updateUser(userId, user);
        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(userId, user));
    }

    @Test
    void updateUserByAdmin_positive_updated() throws UserAlreadyExistsException {
        var userId = new UserId("random_username");
        var user = new User(userId, "qwerty123", "Zubenko Mikhail", "client", true);
        userService.updateUserByAdmin(userId, user);
        verify(userRepository).updateUser(userId, user);
    }

    @Test
    void updateUserByAdmin_negative_invalid() throws UserAlreadyExistsException {
        var userId = new UserId("random_username");
        var user = new User(new UserId("random_username_long"), "qwerty123", "Zubenko Mikhail", "client", true);
        assertThrows(InvalidEntityException.class, () -> userService.updateUserByAdmin(userId, user));
        verify(userRepository, never()).updateUser(any(), any());
    }

    @Test
    void updateUserByAdmin_negative_exists() throws UserAlreadyExistsException {
        var userId = new UserId("random_username");
        var newUserId = new UserId("username_exists");
        var user = new User(userId, "qwerty123", "Zubenko Mikhail", "client", true);
        var exception = new UserAlreadyExistsException(newUserId);
        willThrow(exception).given(userRepository).updateUser(userId, user);
        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUserByAdmin(userId, user));
    }
}