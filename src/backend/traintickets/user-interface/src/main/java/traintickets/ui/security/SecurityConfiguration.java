package traintickets.ui.security;

import io.javalin.http.Context;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.jwt.JwtManager;
import traintickets.businesslogic.transport.UserInfo;
import traintickets.security.exception.ForbiddenException;
import traintickets.security.exception.InvalidTokenException;
import traintickets.security.exception.UnauthorizedException;

import java.util.Objects;

public final class SecurityConfiguration {
    private final JwtManager jwtManager;
    private final UniLogger logger;
    private final String userRole, carrierRole, adminRole;

    public SecurityConfiguration(JwtManager jwtManager,
                                 UniLoggerFactory loggerFactory,
                                 String userRole,
                                 String carrierRole,
                                 String adminRole) {
        this.jwtManager = Objects.requireNonNull(jwtManager);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(SecurityConfiguration.class);
        this.userRole = Objects.requireNonNull(userRole);
        this.carrierRole = Objects.requireNonNull(carrierRole);
        this.adminRole = Objects.requireNonNull(adminRole);
    }

    public void unauthorizedOnly(Context ctx) {
        var token = ctx.header("Authorization");
        if (token != null) {
            throw new ForbiddenException();
        }
    }

    public UserInfo authorizedOnly(Context ctx) {
        var token = ctx.header("Authorization");
        if (token == null) {
            throw new UnauthorizedException();
        }
        try {
            return jwtManager.validateToken(token.substring(7));
        } catch (InvalidTokenException e) {
            logger.error("Invalid token: '%s'", e.getMessage());
            throw new ForbiddenException();
        }
    }

    public UserInfo forUser(Context ctx) {
        return forRoles(ctx, userRole, adminRole);
    }

    public UserInfo forCarrier(Context ctx) {
        return forRoles(ctx, carrierRole, adminRole);
    }

    public UserInfo forAdmin(Context ctx) {
        return forRoles(ctx, adminRole);
    }

    private UserInfo forRoles(Context ctx, String... roles) {
        var token = ctx.header("Authorization");
        if (token == null) {
            throw new UnauthorizedException();
        }
        try {
            var userInfo = jwtManager.validateToken(token.substring(7));
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
            return userInfo;
        } catch (InvalidTokenException e) {
            logger.error("Invalid token: '%s'", e.getMessage());
            throw new ForbiddenException();
        }
    }
}
