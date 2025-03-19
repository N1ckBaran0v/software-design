package traintickets.businesslogic.exception;

import java.util.Objects;

public final class UserWasBannedException extends RuntimeException {
    public UserWasBannedException(String username) {
        super(String.format("User with username %s was banned", Objects.requireNonNull(username)));
    }
}
