package traintickets.security.filter;

import traintickets.businesslogic.transport.UserInfo;
import traintickets.router.HttpRouter;

import java.util.function.Consumer;

public final class SecurityFilterChainImplementation implements SecurityFilterChain {
    private final HttpFilter filter;

    public SecurityFilterChainImplementation(HttpRouter<Consumer<UserInfo>> router) {
        this.filter = HttpFilter.builder(router)
                .build();
    }

    @Override
    public HttpFilter getFilter() {
        return filter;
    }
}
