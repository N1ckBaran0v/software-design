package traintickets.ui.exception;

public final class InvalidQueryParameterException extends RuntimeException {
    public InvalidQueryParameterException(String name) {
        super(String.format("Invalid query parameter '%s'", name));
    }
}
