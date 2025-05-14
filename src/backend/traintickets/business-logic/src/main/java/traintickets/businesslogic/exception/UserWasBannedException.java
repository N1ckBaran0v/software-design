package traintickets.businesslogic.exception;

import traintickets.businesslogic.model.UserId;

import java.util.Objects;

public final class UserWasBannedException extends RuntimeException {
    public UserWasBannedException(String username) {
        super(String.format("User with username '%s' was banned", Objects.requireNonNull(username)));
    }

    public UserWasBannedException(UserId userId) {
        super(String.format("User with id '%s' was banned", Objects.requireNonNull(userId).id()));
    }
}
