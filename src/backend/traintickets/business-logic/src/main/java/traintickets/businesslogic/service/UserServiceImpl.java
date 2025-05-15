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

    public UserServiceImpl(UserRepository userRepository, JwtManager jwtManager) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.jwtManager = Objects.requireNonNull(jwtManager);
    }

    @Override
    public void createUser(User user) {
        user.validate();
        userRepository.addUser(user);
    }

    @Override
    public void deleteUser(UserId userId) {
        userRepository.deleteUser(userId);
        jwtManager.updateUser(userId);
    }

    @Override
    public TransportUser getUser(UserInfo userInfo, UserId userId) {
        if (!userId.equals(userInfo.userId())) {
            throw new InvalidEntityException("Invalid userId");
        }
        var user = userRepository.getUserById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id '%s' not found", userId.id())));
        if (!user.active()) {
            throw new UserWasBannedException(userId);
        }
        return TransportUser.from(user);
    }

    @Override
    public User getUserByAdmin(String username) {
        return userRepository.getUserByUsername(username).orElseThrow(
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
        userRepository.updateUserPartially(user);
        jwtManager.updateUser(user.id());
    }

    @Override
    public void updateUserByAdmin(User user) {
        user.validate();
        if (user.id() == null) {
            throw new InvalidEntityException("All data required");
        }
        userRepository.updateUserCompletely(user);
        jwtManager.updateUser(user.id());
    }
}
