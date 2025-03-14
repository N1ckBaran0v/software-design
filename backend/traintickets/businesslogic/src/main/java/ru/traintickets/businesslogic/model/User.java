package ru.traintickets.businesslogic.model;

public record User(User username, String password, String name, String role, boolean active) {
}
