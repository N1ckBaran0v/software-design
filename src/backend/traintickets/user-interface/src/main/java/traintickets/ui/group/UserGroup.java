package traintickets.ui.group;

import io.javalin.http.HandlerType;
import traintickets.ui.controller.UserController;
import traintickets.ui.exception.QueryParameterNotFoundException;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class UserGroup extends AbstractEndpointGroup {
    private final UserController userController;
    private final SecurityConfiguration securityConfiguration;

    public UserGroup(UserController userController, SecurityConfiguration securityConfiguration) {
        super("/api/users");
        this.userController = Objects.requireNonNull(userController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        before(ctx -> {
            if (ctx.method().equals(HandlerType.GET)) {
                var username = ctx.queryParam("login");
                var raceId = ctx.queryParam("raceId");
                if (username == null && raceId != null) {
                    securityConfiguration.forCarrier(ctx);
                } else if (username != null && raceId == null) {
                    securityConfiguration.authorizedOnly(ctx);
                } else {
                    throw new QueryParameterNotFoundException("login && raceId");
                }
            } else {
                securityConfiguration.authorizedOnly(ctx);
            }
        });
        post(userController::createUser);
        delete("/{userId}", userController::deleteUser);
        get(userController::getUsers);
        put("/{userId}", userController::updateUser);
    }
}
