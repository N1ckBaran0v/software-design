package traintickets.ui.security;

import io.javalin.http.Context;
import traintickets.businesslogic.session.SessionManager;
import traintickets.security.exception.ForbiddenException;
import traintickets.security.exception.UnauthorizedException;

import java.util.Objects;

public final class SecurityConfiguration {
    private final SessionManager sessionManager;
    private final String userRole, carrierRole, adminRole;

    public SecurityConfiguration(SessionManager sessionManager, String userRole, String carrierRole, String adminRole) {
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.userRole = Objects.requireNonNull(userRole);
        this.carrierRole = Objects.requireNonNull(carrierRole);
        this.adminRole = Objects.requireNonNull(adminRole);
    }

    public void checkSessionId(Context ctx) {
        if (ctx.cookie("sessionId") == null) {
            ctx.cookieStore().set("sessionId", sessionManager.generateSessionId());
        }
    }

    public void unauthorizedOnly(Context ctx) {
        if (sessionManager.getUserInfo(ctx.cookie("sessionId")) != null) {
            throw new ForbiddenException();
        }
    }

    public void authorizedOnly(Context ctx) {
        if (sessionManager.getUserInfo(ctx.cookie("sessionId")) == null) {
            throw new UnauthorizedException();
        }
    }

    public void forUser(Context ctx) {
        var userInfo = sessionManager.getUserInfo(ctx.cookie("sessionId"));
        if (userInfo.role() == null) {
            throw new UnauthorizedException();
        }
        if (!(userInfo.role().equals(userRole) || userInfo.role().equals(adminRole))) {
            throw new ForbiddenException();
        }
    }

    public void forCarrier(Context ctx) {
        var userInfo = sessionManager.getUserInfo(ctx.cookie("sessionId"));
        if (userInfo.role() == null) {
            throw new UnauthorizedException();
        }
        if (!(userInfo.role().equals(carrierRole) || userInfo.role().equals(adminRole))) {
            throw new ForbiddenException();
        }
    }

    public void forAdmin(Context ctx) {
        var userInfo = sessionManager.getUserInfo(ctx.cookie("sessionId"));
        if (userInfo.role() == null) {
            throw new UnauthorizedException();
        }
        if (!userInfo.role().equals(adminRole)) {
            throw new ForbiddenException();
        }
    }
}
