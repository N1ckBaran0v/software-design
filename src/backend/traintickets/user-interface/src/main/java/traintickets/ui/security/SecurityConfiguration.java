package traintickets.ui.security;

import io.javalin.http.Context;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.session.SessionManager;
import traintickets.security.exception.ForbiddenException;
import traintickets.security.exception.UnauthorizedException;

import java.util.Objects;

public final class SecurityConfiguration {
    private final SessionManager sessionManager;
    private final UniLogger logger;
    private final String userRole, carrierRole, adminRole;

    public SecurityConfiguration(SessionManager sessionManager,
                                 UniLoggerFactory loggerFactory,
                                 String userRole,
                                 String carrierRole,
                                 String adminRole) {
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(SecurityConfiguration.class);
        this.userRole = Objects.requireNonNull(userRole);
        this.carrierRole = Objects.requireNonNull(carrierRole);
        this.adminRole = Objects.requireNonNull(adminRole);
    }

    public void checkSessionId(Context ctx) {
        if (!ctx.sessionAttributeMap().containsKey("id")) {
            ctx.sessionAttribute("id", sessionManager.generateSessionId());
        }
        logger.debug("%s called %s:%s", ctx.sessionAttributeMap().get("id").toString(), ctx.method(), ctx.path());
    }

    public void unauthorizedOnly(Context ctx) {
        if (sessionManager.getUserInfo(ctx.sessionAttributeMap().get("id").toString()) != null) {
            throw new ForbiddenException();
        }
    }

    public void authorizedOnly(Context ctx) {
        if (sessionManager.getUserInfo(ctx.sessionAttributeMap().get("id").toString()) == null) {
            throw new UnauthorizedException();
        }
    }

    public void forUser(Context ctx) {
        forRoles(ctx, userRole, adminRole);
    }

    public void forCarrier(Context ctx) {
        forRoles(ctx, carrierRole, adminRole);
    }

    public void forAdmin(Context ctx) {
        forRoles(ctx, adminRole);
    }

    private void forRoles(Context ctx, String... roles) {
        var userInfo = sessionManager.getUserInfo(ctx.sessionAttributeMap().get("id").toString());
        if (userInfo.role() == null) {
            throw new UnauthorizedException();
        }
        var flag = false;
        for (var role : roles) {
            if (userInfo.role().equals(role)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new ForbiddenException();
        }
    }
}
