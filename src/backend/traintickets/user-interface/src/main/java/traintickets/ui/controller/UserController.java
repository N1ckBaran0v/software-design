package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.RaceService;
import traintickets.businesslogic.api.UserService;
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
    private final String adminRole;

    public UserController(UserService userService,
                          RaceService raceService,
                          SessionManager sessionManager,
                          String adminRole) {
        this.userService = Objects.requireNonNull(userService);
        this.raceService = Objects.requireNonNull(raceService);
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.adminRole = Objects.requireNonNull(adminRole);
    }

    public void createUser(Context ctx) {
        userService.createUser(ctx.bodyAsClass(User.class));
    }

    public void deleteUser(Context ctx) {
        userService.deleteUser(new UserId(ctx.pathParam("userId")));
    }

    public void getUsers(Context ctx) {
        var username = ctx.queryParam("username");
        if (username == null) {
            var raceId = ctx.queryParam("raceId");
            ctx.json(raceService.getRace(ctx.cookie("sessionId"), new RaceId(raceId)));
        }
        var role = sessionManager.getUserInfo(ctx.cookie("sessionId")).role();
        if (role.equals(adminRole)) {
            ctx.json(userService.getUserByAdmin(username));
        } else {
            ctx.json(userService.getUser(username));
        }
    }

    public void updateUser(Context ctx) {
        var role = sessionManager.getUserInfo(ctx.cookie("sessionId")).role();
        if (role.equals(adminRole)) {
            userService.updateUserByAdmin(ctx.bodyAsClass(User.class));
        } else {
            userService.updateUser(ctx.cookie("sessionId"), ctx.bodyAsClass(TransportUser.class));
        }
    }
}