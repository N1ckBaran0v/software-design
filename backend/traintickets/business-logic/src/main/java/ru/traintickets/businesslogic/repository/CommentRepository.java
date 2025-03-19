package ru.traintickets.businesslogic.repository;

import ru.traintickets.businesslogic.model.CommentId;
import ru.traintickets.businesslogic.model.Comment;
import ru.traintickets.businesslogic.model.TrainId;

public interface CommentRepository {
    void addComment(Comment comment);
    Iterable<Comment> getComments(TrainId trainId);
    void deleteComment(CommentId commentId);
}
