package traintickets.security.exception;

public final class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super(String.format("Invalid token: %s", token));
    }
}
