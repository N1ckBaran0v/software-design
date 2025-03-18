package ru.traintickets.businesslogic.exception;

import ru.traintickets.businesslogic.model.UserId;

import java.util.Objects;

public final class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(UserId userId) {
        super(String.format("User with username \"%s\" already exists", Objects.requireNonNull(userId).id()));
    }
}
