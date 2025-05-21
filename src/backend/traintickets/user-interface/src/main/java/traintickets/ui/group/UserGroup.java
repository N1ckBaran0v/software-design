package traintickets.ui.group;

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
    public void addEndpoints() {;
        post(ctx -> {
            securityConfiguration.forAdmin(ctx);
            userController.createUser(ctx);
        });
        delete("/{userId}", ctx -> {
            securityConfiguration.forAdmin(ctx);
            userController.deleteUser(ctx);
        });
        get(ctx -> {
            var username = ctx.queryParam("username");
            var raceId = ctx.queryParam("raceId");
            if (username == null && raceId != null) {
                securityConfiguration.forCarrier(ctx);
                userController.getUsers(ctx);
            } else if (username != null && raceId == null) {
                securityConfiguration.forAdmin(ctx);
                userController.getUsers(ctx);
            } else {
                throw new QueryParameterNotFoundException("login && raceId");
            }
        });
        get("/{userId}", ctx -> userController.getUser(ctx, securityConfiguration.authorizedOnly(ctx)));
        put("/{userId}", ctx -> userController.updateUser(ctx, securityConfiguration.authorizedOnly(ctx)));
    }
}
