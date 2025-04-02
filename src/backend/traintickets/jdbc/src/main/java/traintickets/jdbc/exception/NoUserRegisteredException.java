package traintickets.jdbc.exception;

public final class NoUserRegisteredException extends RuntimeException {
    public NoUserRegisteredException() {
        super("No user registered in database parameters");
    }
}
