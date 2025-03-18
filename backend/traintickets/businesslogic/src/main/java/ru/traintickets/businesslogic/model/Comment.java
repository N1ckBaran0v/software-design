package ru.traintickets.businesslogic.model;

public record Comment(CommentId id, UserId author, TrainId train, int score, String text) {
}
