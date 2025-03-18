package ru.traintickets.businesslogic.model;

public record User(UserId username, String password, String name, String role, boolean active) {
}
