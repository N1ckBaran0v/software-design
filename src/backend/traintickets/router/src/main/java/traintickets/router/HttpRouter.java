package traintickets.router;

public interface HttpRouter<T> {
    void register(String method, String path, T value);
    HttpRouterValue<T> get(String httpMethod, String path);
}
