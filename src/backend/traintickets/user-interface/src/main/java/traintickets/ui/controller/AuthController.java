package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.AuthService;
import traintickets.businesslogic.transport.LoginForm;
import traintickets.businesslogic.transport.RegisterForm;

import java.util.Objects;

public final class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = Objects.requireNonNull(authService);
    }

    public void register(Context ctx) {
        authService.register(ctx.cookie("sessionId"), ctx.bodyAsClass(RegisterForm.class));
    }

    public void login(Context ctx) {
        authService.login(ctx.cookie("sessionId"), ctx.bodyAsClass(LoginForm.class));
    }

    public void logout(Context ctx) {
        authService.logout(ctx.cookie("sessionId"));
    }
}