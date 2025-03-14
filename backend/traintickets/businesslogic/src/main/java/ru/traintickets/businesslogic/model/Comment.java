package ru.traintickets.businesslogic.model;

public record Comment(CommId id, UserId author, TrainId train, int score, String text) {
}
