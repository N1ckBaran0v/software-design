package traintickets.businesslogic.exception;

import java.util.Objects;

public final class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String username) {
        super(String.format("User with username \"%s\" already exists", Objects.requireNonNull(username)));
    }
}
