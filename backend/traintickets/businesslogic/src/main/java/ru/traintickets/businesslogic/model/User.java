package ru.traintickets.businesslogic.model;

import ru.traintickets.businesslogic.exception.InvalidEntityException;

import java.util.regex.Pattern;

public record User(UserId username, String password, String name, String role, boolean active) {
    private static final Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9_-]{3,15}$");
    private static final Pattern passwordPattern = Pattern.compile("^.{8,20}$");
    private static final Pattern namePattern = Pattern.compile("^.{3,30}$");

    public void validate() {
        if (username == null || password == null || name == null || role == null) {
            throw new InvalidEntityException("Invalid user data");
        }
        if (username.id() == null || !usernamePattern.matcher(username.id()).matches()) {
            throw new InvalidEntityException("Invalid username");
        }
        if (!passwordPattern.matcher(password).matches()) {
            throw new InvalidEntityException("Invalid password");
        }
        if (!namePattern.matcher(name).matches()) {
            throw new InvalidEntityException("Invalid name");
        }
        if (!active) {
            throw new InvalidEntityException("Inactive user");
        }
    }
}
