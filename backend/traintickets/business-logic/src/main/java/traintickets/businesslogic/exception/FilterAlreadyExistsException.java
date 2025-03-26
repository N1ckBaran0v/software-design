package traintickets.businesslogic.exception;

public class FilterAlreadyExistsException extends RuntimeException {
    public FilterAlreadyExistsException(String filterName) {
        super(String.format("Filter '%s' already exists", filterName));
    }
}
