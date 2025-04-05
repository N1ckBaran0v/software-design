package traintickets.router;

public final class MethodNotAllowedException extends RuntimeException {
    MethodNotAllowedException(String method) {
        super(String.format("Method '%s' is not allowed", method == null ? "ALL" : method));
    }
}
