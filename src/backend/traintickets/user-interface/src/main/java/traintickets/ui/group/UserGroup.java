package traintickets.ui.group;

import traintickets.ui.controller.UserController;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class UserGroup extends AbstractEndpointGroup {
    private final UserController userController;

    public UserGroup(UserController userController) {
        super("/api/users");
        this.userController = Objects.requireNonNull(userController);
    }

    @Override
    public void addEndpoints() {
        post(userController::createUser);
        delete("/{userId}", userController::deleteUser);
        get(userController::getUsers);
        put("/{userId}", userController::updateUser);
    }
}
