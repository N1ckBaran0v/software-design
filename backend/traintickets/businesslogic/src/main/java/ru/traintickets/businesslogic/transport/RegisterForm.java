package ru.traintickets.businesslogic.transport;

public record RegisterForm(String username, String password, String confirmPassword, String name) {
}
