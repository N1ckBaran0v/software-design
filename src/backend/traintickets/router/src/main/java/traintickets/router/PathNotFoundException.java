package traintickets.router;

public final class PathNotFoundException extends RuntimeException {
    PathNotFoundException() {
        super("Path not found");
    }
}
