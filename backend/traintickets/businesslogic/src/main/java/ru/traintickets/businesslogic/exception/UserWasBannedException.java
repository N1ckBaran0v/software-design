package ru.traintickets.businesslogic.exception;

import ru.traintickets.businesslogic.model.UserId;

import java.util.Objects;

public final class UserWasBannedException extends RuntimeException {
    public UserWasBannedException(UserId userId) {
        super(String.format("User with username %s was banned", Objects.requireNonNull(userId).id()));
    }
}
