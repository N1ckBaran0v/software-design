package ru.traintickets.businesslogic.transport;

import ru.traintickets.businesslogic.model.UserId;

public record LoginForm(UserId username, String password) {
}
