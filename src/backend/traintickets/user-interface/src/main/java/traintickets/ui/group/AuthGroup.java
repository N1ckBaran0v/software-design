package traintickets.ui.group;

import traintickets.ui.controller.AuthController;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.post;

public final class AuthGroup extends AbstractEndpointGroup {
    private final AuthController authController;

    public AuthGroup(AuthController authController) {
        super("/auth");
        this.authController = Objects.requireNonNull(authController);
    }

    @Override
    public void addEndpoints() {
        post("/register", authController::register);
        post("/login", authController::login);
        post("/logout", authController::logout);
    }
}
