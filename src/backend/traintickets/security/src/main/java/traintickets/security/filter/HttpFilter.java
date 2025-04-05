package traintickets.security.filter;

import traintickets.businesslogic.transport.UserInfo;
import traintickets.router.HttpRouter;
import traintickets.router.MethodNotAllowedException;
import traintickets.router.PathNotFoundException;

import java.util.Objects;
import java.util.function.Consumer;

public final class HttpFilter {
    private final HttpRouter<Consumer<UserInfo>> router;

    HttpFilter(HttpRouter<Consumer<UserInfo>> router) {
        this.router = Objects.requireNonNull(router);
    }

    static HttpFilterBuilder builder(HttpRouter<Consumer<UserInfo>> router) {
        return new HttpFilterBuilder(router);
    }

    public void filter(String httpMethod, String url, UserInfo user) {
        try {
            router.get(httpMethod, url).value().accept(user);
        } catch (PathNotFoundException | MethodNotAllowedException ignored) {
        }
    }
}
