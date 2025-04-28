package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.AuthService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.transport.LoginForm;
import traintickets.businesslogic.transport.RegisterForm;

import java.util.Map;
import java.util.Objects;

public final class AuthController {
    private final AuthService authService;
    private final UniLogger logger;

    public AuthController(AuthService authService, UniLoggerFactory loggerFactory) {
        this.authService = Objects.requireNonNull(authService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(AuthController.class);
    }

    public void register(Context ctx) {
        var form = ctx.bodyAsClass(RegisterForm.class);
        logger.debug("register form: %s", form);
        ctx.json(Map.of("token", authService.register(form)));
        logger.debug("registered");
    }

    public void login(Context ctx) {
        var form = ctx.bodyAsClass(LoginForm.class);
        logger.debug("login form: %s", form);
        ctx.json(Map.of("token", authService.login(form)));
        logger.debug("logged in");
    }

    public void logout(Context ctx) {
        authService.logout(Objects.requireNonNull(ctx.header("Authorization")).substring(7));
        logger.debug("logged out");
    }
}