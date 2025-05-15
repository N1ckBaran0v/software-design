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
import traintickets.businesslogic.jwt.JwtManager;
import traintickets.businesslogic.transport.TransportUser;
import traintickets.businesslogic.transport.UserInfo;

import java.util.Optional;

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
    private JwtManager jwtManager;

    private final String systemRole = "system_role";
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, jwtManager, systemRole);
    }

    @Test
    void createUser_positive_created() {
        var user = new User(null, "random_username", "qwerty123", "Zubenko Mikhail", "client", true);
        var userInfo = new UserInfo(new UserId("1"), "admin_role");
        userService.createUser(userInfo, user);
        verify(userRepository).addUser(userInfo.role(), user);
    }

    @Test
    void createUser_negative_invalid() {
        var user = new User(null, "random_username_long", "qwerty123", "Zubenko Mikhail", "client", true);
        var userInfo = new UserInfo(new UserId("1"), "admin_role");
        assertThrows(InvalidEntityException.class, () -> userService.createUser(userInfo, user));
        verify(userRepository, never()).addUser(any(), any());
    }

    @Test
    void deleteUser_positive_deleted() {
        var userId = new UserId("1");
        var userInfo = new UserInfo(new UserId("1"), "admin_role");
        userService.deleteUser(userInfo, userId);
        verify(userRepository).deleteUser(userInfo.role(), userId);
        verify(jwtManager).updateUser(userId);
    }

    @Test
    void getUser_positive_found() {
        var userId = new UserId("1");
        var user = new User(userId, "random_username", "qwerty123", "Zubenko Mikhail", "client", true);
        var userInfo = new UserInfo(userId, "user_role");
        given(userRepository.getUserById(systemRole, userId)).willReturn(Optional.of(user));
        var result = userService.getUser(userInfo, userId);
        assertEquals(TransportUser.from(user), result);
    }

    @Test
    void getUser_negative_another() {
        var userInfo = new UserInfo(new UserId("2"), "user_role");
        assertThrows(InvalidEntityException.class, () -> userService.getUser(userInfo, new UserId("1")));
        verify(userRepository, never()).getUserById(any(), any());
    }

    @Test
    void getUser_negative_notFound() {
        var userId = new UserId("1");
        var userInfo = new UserInfo(userId, "user_role");
        given(userRepository.getUserById(systemRole, userId)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(userInfo, userId));
    }

    @Test
    void getUser_negative_banned() {
        var userId = new UserId("1");
        var user = new User(userId, "random_username", "qwerty123", "Zubenko Mikhail", "client", false);
        var userInfo = new UserInfo(userId, "user_role");
        given(userRepository.getUserById(systemRole, userId)).willReturn(Optional.of(user));
        assertThrows(UserWasBannedException.class, () -> userService.getUser(userInfo, userId));
    }

    @Test
    void getUserByAdmin_positive_found() {
        var username = "random_username";
        var user = new User(new UserId("1"), username, "qwerty123", "Zubenko Mikhail", "client", true);
        var userInfo = new UserInfo(new UserId("1"), "admin_role");
        given(userRepository.getUserByUsername(userInfo.role(), username)).willReturn(Optional.of(user));
        var result = userService.getUserByAdmin(userInfo, username);
        assertSame(user, result);
    }

    @Test
    void getUserByAdmin_negative_notFound() {
        var username = "random_username";
        var userInfo = new UserInfo(new UserId("1"), "admin_role");
        given(userRepository.getUserByUsername(userInfo.role(), username)).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserByAdmin(userInfo, username));
    }

    @Test
    void getUserByAdmin_positive_banned() {
        var username = "random_username";
        var user = new User(new UserId("1"), username, "qwerty123", "Zubenko Mikhail", "client", false);
        var userInfo = new UserInfo(new UserId("1"), "admin_role");
        given(userRepository.getUserByUsername(userInfo.role(), username)).willReturn(Optional.of(user));
        var result = userService.getUserByAdmin(userInfo, username);
        assertSame(result, user);
    }

    @Test
    void updateUser_positive_updated() {
        var userId = new UserId("1");
        var user = new TransportUser(userId, "random_username", "qwerty123", "Zubenko Mikhail");
        var userInfo = new UserInfo(userId, "user_role");
        userService.updateUser(userInfo, user);
        verify(userRepository).updateUserPartially(systemRole, user);
        verify(jwtManager).updateUser(user.id());
    }

    @Test
    void updateUser_negative_invalid() {
        var user = new TransportUser(new UserId("1"), "random_username", "qwerty123", "Zubenko Mikhail");
        var userInfo = new UserInfo(new UserId("2"), "user_role");
        assertThrows(InvalidEntityException.class, () -> userService.updateUser(userInfo ,user));
        verify(userRepository, never()).updateUserPartially(any(), any());
        verify(jwtManager, never()).updateUser(any());
    }

    @Test
    void updateUserByAdmin_positive_updated() {
        var user = new User(new UserId("1"), "random_username", "qwerty123", "Zubenko Mikhail", "admin", true);
        var userInfo = new UserInfo(new UserId("2"), "admin_role");
        userService.updateUserByAdmin(userInfo, user);
        verify(userRepository).updateUserCompletely(userInfo.role(), user);
        verify(jwtManager).updateUser(user.id());
    }

    @Test
    void updateUserByAdmin_negative_invalid() {
        var user = new User(new UserId("1"), "random_username_long", "qwerty123", "Zubenko Mikhail", "client", true);
        var userInfo = new UserInfo(new UserId("2"), "admin_role");
        assertThrows(InvalidEntityException.class, () -> userService.updateUserByAdmin(userInfo, user));
        verify(userRepository, never()).updateUserCompletely(any(), any());
        verify(jwtManager, never()).updateUser(any());
    }

    @Test
    void updateUserByAdmin_positive_banned() {
        var user = new User(new UserId("1"), "random_username", "qwerty123", "Zubenko Mikhail", "client", false);
        var userInfo = new UserInfo(new UserId("2"), "admin_role");
        userService.updateUserByAdmin(userInfo, user);
        verify(userRepository).updateUserCompletely(userInfo.role(), user);
        verify(jwtManager).updateUser(user.id());
    }
}