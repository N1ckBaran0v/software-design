package traintickets.ui.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import traintickets.businesslogic.api.RaceService;
import traintickets.businesslogic.api.UserService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.RaceId;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.session.SessionManager;
import traintickets.businesslogic.transport.TransportUser;

import java.util.Objects;

public final class UserController {
    private final UserService userService;
    private final RaceService raceService;
    private final SessionManager sessionManager;
    private final UniLogger logger;
    private final String adminRole;

    public UserController(UserService userService,
                          RaceService raceService,
                          SessionManager sessionManager,
                          UniLoggerFactory loggerFactory,
                          String adminRole) {
        this.userService = Objects.requireNonNull(userService);
        this.raceService = Objects.requireNonNull(raceService);
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(UserController.class);
        this.adminRole = Objects.requireNonNull(adminRole);
    }

    public void createUser(Context ctx) {
        var user = ctx.bodyAsClass(User.class);
        logger.debug("user: %s", user);
        userService.createUser(user);
        ctx.status(HttpStatus.CREATED);
        logger.debug("user created");
    }

    public void deleteUser(Context ctx) {
        var userId = ctx.pathParam("userId");
        logger.debug("user: %s", userId);
        userService.deleteUser(new UserId(userId));
        ctx.status(HttpStatus.NO_CONTENT);
        logger.debug("user deleted");
    }

    public void getUsers(Context ctx) {
        var username = ctx.queryParam("login");
        if (username == null) {
            var raceId = ctx.queryParam("raceId");
            logger.debug("raceId: %s", raceId);
            ctx.json(raceService.getPassengers(ctx.sessionAttributeMap().get("id").toString(), new RaceId(raceId)));
            logger.debug("users got");
        } else {
            logger.debug("login: %s", username);
            var role = sessionManager.getUserInfo(ctx.sessionAttributeMap().get("id").toString()).role();
            if (role.equals(adminRole)) {
                ctx.json(userService.getUserByAdmin(username));
            } else {
                ctx.json(userService.getUser(username));
            }
            logger.debug("user got");
        }
    }

    public void updateUser(Context ctx) {
        var role = sessionManager.getUserInfo(ctx.sessionAttributeMap().get("id").toString()).role();
        if (role.equals(adminRole)) {
            var user = ctx.bodyAsClass(User.class);
            logger.debug("user: %s", user);
            userService.updateUserByAdmin(user);
        } else {
            var user = ctx.bodyAsClass(TransportUser.class);
            logger.debug("user: %s", user);
            userService.updateUser(ctx.sessionAttributeMap().get("id").toString(), user);
        }
        logger.debug("user updated");
    }
}