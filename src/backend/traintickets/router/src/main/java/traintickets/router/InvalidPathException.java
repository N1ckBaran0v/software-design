package traintickets.router;

public final class InvalidPathException extends RuntimeException {
    InvalidPathException(String path) {
        super(String.format("Invalid path: '%s'", path));
    }
}
