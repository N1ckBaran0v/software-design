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
        super("/users");
        this.userController = Objects.requireNonNull(userController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        before(ctx -> {
            if (ctx.method().equals(HandlerType.POST) || ctx.method().equals(HandlerType.DELETE)) {
                securityConfiguration.forAdmin(ctx);
            }
        });
        post(userController::createUser);
        delete("/{userId}", userController::deleteUser);
        get(ctx -> {
            var username = ctx.queryParam("username");
            var raceId = ctx.queryParam("raceId");
            if (username == null && raceId != null) {
                userController.getUsers(ctx, securityConfiguration.forCarrier(ctx));
            } else if (username != null && raceId == null) {
                userController.getUsers(ctx, securityConfiguration.forAdmin(ctx));
            } else {
                throw new QueryParameterNotFoundException("login && raceId");
            }
        });
        get("/{userId}", ctx -> userController.getUser(ctx, securityConfiguration.authorizedOnly(ctx)));
        put("/{userId}", ctx -> userController.updateUser(ctx, securityConfiguration.authorizedOnly(ctx)));
    }
}
