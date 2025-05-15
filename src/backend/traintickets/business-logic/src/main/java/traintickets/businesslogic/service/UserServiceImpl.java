package traintickets.businesslogic.service;

import traintickets.businesslogic.api.UserService;
import traintickets.businesslogic.exception.EntityNotFoundException;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.exception.UserWasBannedException;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.UserRepository;
import traintickets.businesslogic.jwt.JwtManager;
import traintickets.businesslogic.transport.TransportUser;
import traintickets.businesslogic.transport.UserInfo;

import java.util.Objects;

public final class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtManager jwtManager;
    private final String systemRole;

    public UserServiceImpl(UserRepository userRepository, JwtManager jwtManager, String systemRole) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.jwtManager = Objects.requireNonNull(jwtManager);
        this.systemRole = Objects.requireNonNull(systemRole);
    }

    @Override
    public void createUser(UserInfo userInfo, User user) {
        user.validate();
        userRepository.addUser(userInfo.role(), user);
    }

    @Override
    public void deleteUser(UserInfo userInfo, UserId userId) {
        userRepository.deleteUser(userInfo.role(), userId);
        jwtManager.updateUser(userId);
    }

    @Override
    public TransportUser getUser(UserInfo userInfo, UserId userId) {
        if (!userId.equals(userInfo.userId())) {
            throw new InvalidEntityException("Invalid userId");
        }
        var user = userRepository.getUserById(systemRole, userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id '%s' not found", userId.id())));
        if (!user.active()) {
            throw new UserWasBannedException(userId);
        }
        return TransportUser.from(user);
    }

    @Override
    public User getUserByAdmin(UserInfo userInfo, String username) {
        return userRepository.getUserByUsername(userInfo.role(), username).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with username '%s' not found", username)));
    }

    @Override
    public void updateUser(UserInfo userInfo, TransportUser user) {
        user.toUser("someRole", true).validate();
        if (user.id() == null) {
            throw new InvalidEntityException("All data required");
        }
        var userId = userInfo.userId();
        if (!userId.equals(user.id())) {
            throw new InvalidEntityException(
                    String.format("Invalid userId: expected '%s', but got '%s'", userId.id(), user.id().id()));
        }
        userRepository.updateUserPartially(systemRole, user);
        jwtManager.updateUser(user.id());
    }

    @Override
    public void updateUserByAdmin(UserInfo userInfo, User user) {
        user.validate();
        if (user.id() == null) {
            throw new InvalidEntityException("All data required");
        }
        userRepository.updateUserCompletely(userInfo.role(), user);
        jwtManager.updateUser(user.id());
    }
}
