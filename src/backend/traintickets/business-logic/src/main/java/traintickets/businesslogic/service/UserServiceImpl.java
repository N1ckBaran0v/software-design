package traintickets.businesslogic.service;

import traintickets.businesslogic.api.UserService;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.exception.UserWasBannedException;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.businesslogic.session.SessionManager;
import traintickets.businesslogic.transport.UserInfo;

import java.util.Objects;
import java.util.UUID;

public final class UserServiceImpl implements UserService {
//    private final UserRepository userRepository;
//    private final SessionManager sessionManager;
//    private final String systemRole;
//
//    public UserServiceImpl(UserRepository userRepository, SessionManager sessionManager, String systemRole) {
//        this.userRepository = Objects.requireNonNull(userRepository);
//        this.sessionManager = Objects.requireNonNull(sessionManager);
//        this.systemRole = Objects.requireNonNull(systemRole);
//    }
//
//    @Override
//    public void createUser(User user) {
//        user.validate();
//        userRepository.addUser(systemRole, user);
//    }
//
//    @Override
//    public void deleteUser(UserId userId) {
//        userRepository.deleteUser(systemRole, userId);
//        sessionManager.endSessions(userId);
//    }
//
//    @Override
//    public User getUser(String username) {
//        var result = userRepository.getUser(systemRole, username).orElseThrow(
//                () -> new EntityNotFoundException(String.format("User with username '%s' not found", username)));
//        if (!result.active()) {
//            throw new UserWasBannedException(username);
//        }
//        return result;
//    }
//
//    @Override
//    public User getUserByAdmin(String username) {
//        return userRepository.getUser(systemRole, username).orElseThrow(
//                () -> new EntityNotFoundException(String.format("User with username '%s' not found", username)));
//    }
//
//    @Override
//    public void updateUser(UUID sessionId, User user) {
//        user.validate();
//        if (user.id() == null) {
//            throw new InvalidEntityException("All data required");
//        }
//        if (!user.active()) {
//            throw new InvalidEntityException("User can be banned only by admin");
//        }
//        var userInfo = sessionManager.getUserInfo(sessionId);
//        if (!userInfo.role().equals(user.role())) {
//            throw new InvalidEntityException("User role may be changed only by admin");
//        }
//        userRepository.updateUser(systemRole, user);
//    }
//
//    @Override
//    public void updateUserByAdmin(User user) {
//        user.validate();
//        if (user.id() == null) {
//            throw new InvalidEntityException("All data required");
//        }
//        sessionManager.updateUserInfo(new UserInfo(user.id(), user.role()));
//        userRepository.updateUser(systemRole, user);
//    }
}
