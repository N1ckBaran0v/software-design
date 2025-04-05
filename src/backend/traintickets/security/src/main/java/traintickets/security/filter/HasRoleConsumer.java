package traintickets.security.filter;

import traintickets.businesslogic.transport.UserInfo;
import traintickets.security.exception.ForbiddenException;
import traintickets.security.exception.UnauthorizedException;

import java.util.function.Consumer;

final class HasRoleConsumer implements Consumer<UserInfo> {
    private final String[] roles;

    HasRoleConsumer(String[] roles) {
        this.roles = roles;
    }

    @Override
    public void accept(UserInfo user) {
        var flag = false;
        var curr = user.role();
        if (curr == null) {
            throw new UnauthorizedException();
        }
        for (var role : roles) {
            if (curr.equals(role)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new ForbiddenException();
        }
    }
}
