package traintickets.ui.javalin;

public final class JsonMapperException extends RuntimeException {
    public JsonMapperException(String message) {
        super(message);
    }

    public JsonMapperException(Exception cause) {
        super(cause);
    }
}
