package ru.traintickets.businesslogic.service;

import ru.traintickets.businesslogic.api.CommentService;
import ru.traintickets.businesslogic.model.CommId;
import ru.traintickets.businesslogic.model.Comment;
import ru.traintickets.businesslogic.model.TrainId;
import ru.traintickets.businesslogic.repository.CommentRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = Objects.requireNonNull(commentRepository);
    }

    @Override
    public void addComment(Comment comment) {
        commentRepository.addComment(comment);
    }

    @Override
    public List<Comment> getComments(TrainId trainId) {
        return StreamSupport.stream(commentRepository.getComments(trainId).spliterator(), false).toList();
    }

    @Override
    public void deleteComment(CommId commId) {
        commentRepository.deleteComment(commId);
    }
}
