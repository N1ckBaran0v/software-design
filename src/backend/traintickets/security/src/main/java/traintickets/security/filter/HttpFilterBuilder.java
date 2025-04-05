package traintickets.security.filter;

import traintickets.businesslogic.transport.UserInfo;
import traintickets.router.HttpRouter;

import java.util.Objects;
import java.util.function.Consumer;

final class HttpFilterBuilder {
    private final HttpRouter<Consumer<UserInfo>> router;

    HttpFilterBuilder(HttpRouter<Consumer<UserInfo>> router) {
        this.router = router;
    }

    public HttpFilter build() {
        return new HttpFilter(router);
    }

    public HttpFilterBuilder hasRole(String method, String url, String... roles) {
        router.register(Objects.requireNonNull(method), Objects.requireNonNull(url), new HasRoleConsumer(roles));
        return this;
    }

    public HttpFilterBuilder custom(String method, String url, Consumer<UserInfo> consumer) {
        router.register(Objects.requireNonNull(method), Objects.requireNonNull(url), Objects.requireNonNull(consumer));
        return this;
    }
}
