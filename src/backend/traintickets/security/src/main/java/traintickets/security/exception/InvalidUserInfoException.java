package traintickets.security.exception;

import traintickets.businesslogic.transport.UserInfo;

public final class InvalidUserInfoException extends RuntimeException {
    public InvalidUserInfoException() {
        super("UserInfo cannot be null");
    }

    public InvalidUserInfoException(UserInfo userInfo) {
        super(String.format("UserInfo %s is invalid", userInfo));
    }
}
