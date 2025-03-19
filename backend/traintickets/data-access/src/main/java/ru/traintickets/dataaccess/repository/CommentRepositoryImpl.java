package ru.traintickets.dataaccess.repository;

import ru.traintickets.businesslogic.model.Comment;
import ru.traintickets.businesslogic.model.CommentId;
import ru.traintickets.businesslogic.model.TrainId;
import ru.traintickets.businesslogic.repository.CommentRepository;

public final class CommentRepositoryImpl implements CommentRepository {
    @Override
    public void addComment(Comment comment) {
    }

    @Override
    public Iterable<Comment> getComments(TrainId trainId) {
        return null;
    }

    @Override
    public void deleteComment(CommentId commentId) {
    }
}
