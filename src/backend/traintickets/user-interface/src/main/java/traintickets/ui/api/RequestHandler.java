package traintickets.ui.api;

@FunctionalInterface
public interface RequestHandler {
    void accept(HttpContext ctx);
}
