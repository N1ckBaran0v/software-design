package ru.traintickets.businesslogic.exception;

import java.util.Objects;

public final class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(Objects.requireNonNull(message));
    }
}
