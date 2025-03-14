package ru.traintickets.businesslogic.transport;

import ru.traintickets.businesslogic.model.UserId;

public record RegisterForm(UserId username, String password, String confirmPassword, String name) {
}
