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
import traintickets.businesslogic.transport.TransportUser;
import traintickets.businesslogic.transport.UserInfo;
import traintickets.security.exception.ForbiddenException;

import java.util.Objects;

public final class UserController {
    private final UserService userService;
    private final RaceService raceService;
    private final UniLogger logger;
    private final String adminRole;

    public UserController(UserService userService,
                          RaceService raceService,
                          UniLoggerFactory loggerFactory,
                          String adminRole) {
        this.userService = Objects.requireNonNull(userService);
        this.raceService = Objects.requireNonNull(raceService);
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

    public void getUser(Context ctx, UserInfo userInfo) {
        var id = ctx.pathParam("userId");
        logger.debug("user: %s", id);
        var userId = new UserId(id);
        if (!userId.equals(userInfo.userId())) {
            throw new ForbiddenException();
        }
        ctx.json(userService.getUser(userId));
        logger.debug("user got");
    }

    public void getUsers(Context ctx, UserInfo userInfo) {
        var username = ctx.queryParam("username");
        if (username == null) {
            var raceId = ctx.queryParam("raceId");
            logger.debug("raceId: %s", raceId);
            ctx.json(raceService.getPassengers(userInfo, new RaceId(raceId)));
            logger.debug("users got");
        } else {
            logger.debug("username: %s", username);
            ctx.json(userService.getUserByAdmin(username));
            logger.debug("user got");
        }
    }

    public void updateUser(Context ctx, UserInfo userInfo) {
        var id = ctx.pathParam("userId");
        logger.debug("id: %s", id);
        var userId = new UserId(id);
        if (!userId.equals(userInfo.userId())) {
            var role = userInfo.role();
            if (!role.equals(adminRole)) {
                throw new ForbiddenException();
            }
            var user = ctx.bodyAsClass(User.class);
            logger.debug("user: %s", user);
            userService.updateUserByAdmin(user);
        } else {
            var user = ctx.bodyAsClass(TransportUser.class);
            logger.debug("user: %s", user);
            if (!userId.equals(user.id())) {
                throw new ForbiddenException();
            }
            userService.updateUser(userInfo, user);
        }
        logger.debug("user updated");
    }
}