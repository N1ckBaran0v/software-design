package traintickets.jdbc.exception;

import java.util.Objects;

public final class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super(String.format("User '%s' not found", Objects.requireNonNull(username)));
    }
}
