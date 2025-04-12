package traintickets.ui.exception;

public class PathParameterNotFoundException extends RuntimeException {
    public PathParameterNotFoundException(String parameterName) {
        super(String.format("Parameter '%s' not found", parameterName));
    }
}
