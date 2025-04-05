package traintickets.router;

public record HttpRouterValue<T>(String method, String path, T value) {
}
