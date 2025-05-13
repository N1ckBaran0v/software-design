package traintickets.security.exception;

public final class InvalidUserInfoException extends RuntimeException {
    public InvalidUserInfoException() {
        super("UserInfo cannot be null");
    }
}
