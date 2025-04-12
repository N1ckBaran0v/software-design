package traintickets.businesslogic.api;

import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.TrainId;

import java.util.List;

public interface CommentService {
    void addComment(String sessionId, Comment comment);
    List<Comment> getComments(String sessionId, TrainId trainId);
    void deleteComment(String sessionId, CommentId commentId);
}
