package traintickets.ui.group;

import traintickets.ui.controller.AuthController;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.before;
import static io.javalin.apibuilder.ApiBuilder.post;

public final class AuthGroup extends AbstractEndpointGroup {
    private final AuthController authController;
    private final SecurityConfiguration securityConfiguration;

    public AuthGroup(AuthController authController, SecurityConfiguration securityConfiguration) {
        super("/auth");
        this.authController = Objects.requireNonNull(authController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        before("/register", securityConfiguration::unauthorizedOnly);
        post("/register", authController::register);
        before("/login", securityConfiguration::unauthorizedOnly);
        post("/login", authController::login);
    }
}
