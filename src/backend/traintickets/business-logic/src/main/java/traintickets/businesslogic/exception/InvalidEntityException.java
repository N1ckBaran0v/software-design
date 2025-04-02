package traintickets.businesslogic.exception;

import java.util.Objects;

public final class InvalidEntityException extends RuntimeException {
    public InvalidEntityException(String message) {
        super(Objects.requireNonNull(message));
    }
}
