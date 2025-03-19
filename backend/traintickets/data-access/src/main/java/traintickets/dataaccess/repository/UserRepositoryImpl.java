package traintickets.dataaccess.repository;

import traintickets.businesslogic.exception.UserAlreadyExistsException;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.UserRepository;

import java.util.Optional;

public final class UserRepositoryImpl implements UserRepository {
    @Override
    public void addUser(User user) throws UserAlreadyExistsException {
    }

    @Override
    public Optional<User> getUser(String username) {
        return Optional.empty();
    }

    @Override
    public Iterable<User> getUsers(Iterable<UserId> userIds) {
        return null;
    }

    @Override
    public void updateUser(User user) throws UserAlreadyExistsException {
    }

    @Override
    public void deleteUser(UserId userId) {
    }
}
