package ru.traintickets.businesslogic.api;

import ru.traintickets.businesslogic.model.CommentId;
import ru.traintickets.businesslogic.model.Comment;
import ru.traintickets.businesslogic.model.TrainId;

import java.util.List;

public interface CommentService {
    void addComment(Comment comment);
    List<Comment> getComments(TrainId trainId);
    void deleteComment(CommentId commentId);
}
