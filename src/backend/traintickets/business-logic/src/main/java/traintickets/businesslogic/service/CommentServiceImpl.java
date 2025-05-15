package traintickets.businesslogic.service;

import traintickets.businesslogic.api.CommentService;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.CommentRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public final class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = Objects.requireNonNull(commentRepository);
    }

    @Override
    public void addComment(UserId userId, Comment comment) {
        comment.validate();
        if (!userId.equals(comment.author())) {
            throw new InvalidEntityException("Invalid author");
        }
        commentRepository.addComment(comment);
    }

    @Override
    public List<Comment> getComments(TrainId trainId) {
        return StreamSupport.stream(commentRepository.getComments(trainId).spliterator(), false).toList();
    }

    @Override
    public void deleteComment(CommentId commentId) {
        commentRepository.deleteComment(commentId);
    }
}
