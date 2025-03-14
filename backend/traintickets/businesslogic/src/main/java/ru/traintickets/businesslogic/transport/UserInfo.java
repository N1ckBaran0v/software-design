package ru.traintickets.businesslogic.transport;

import ru.traintickets.businesslogic.model.UserId;

import java.util.UUID;

public record UserInfo(UserId username, String role, UUID sessionId) {
}
