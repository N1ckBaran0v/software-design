package traintickets.ui.exception;

public final class QueryParameterNotFoundException extends RuntimeException {
    public QueryParameterNotFoundException(String parameterName) {
        super(String.format("Query parameter '%s' not found", parameterName));
    }
}
