package traintickets.security.exception;

public final class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(RuntimeException e) {
        super(e);
    }

    public InvalidTokenException() {
    }
}
