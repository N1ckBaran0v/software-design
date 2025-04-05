package traintickets.router;

public final class ValueAlreadyRegisteredException extends RuntimeException {
    ValueAlreadyRegisteredException(String method) {
        super(String.format("Dublicated value for method '%s'", method == null ? "ALL" : method));
    }
}
