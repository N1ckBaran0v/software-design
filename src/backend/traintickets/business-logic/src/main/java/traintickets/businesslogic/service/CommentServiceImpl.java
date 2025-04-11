package traintickets.businesslogic.service;

import traintickets.businesslogic.api.CommentService;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.CommentRepository;
import traintickets.businesslogic.session.SessionManager;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.StreamSupport;

public final class CommentServiceImpl implements CommentService {
//    private final CommentRepository commentRepository;
//    private final SessionManager sessionManager;
//
//    public CommentServiceImpl(CommentRepository commentRepository, SessionManager sessionManager) {
//        this.commentRepository = Objects.requireNonNull(commentRepository);
//        this.sessionManager = Objects.requireNonNull(sessionManager);
//    }
//
//    @Override
//    public void addComment(UUID sessionId, Comment comment) {
//        comment.validate();
//        var userInfo = sessionManager.getUserInfo(sessionId);
//        if (!userInfo.userId().equals(comment.author())) {
//            throw new InvalidEntityException("Invalid userId");
//        }
//        commentRepository.addComment(userInfo.role(), comment);
//    }
//
//    @Override
//    public List<Comment> getComments(UUID sessionId, TrainId trainId) {
//        var role = sessionManager.getUserInfo(sessionId).role();
//        return StreamSupport.stream(commentRepository.getComments(role, trainId).spliterator(), false).toList();
//    }
//
//    @Override
//    public void deleteComment(UUID sessionId, CommentId commentId) {
//        var role = sessionManager.getUserInfo(sessionId).role();
//        commentRepository.deleteComment(role, commentId);
//    }
}
