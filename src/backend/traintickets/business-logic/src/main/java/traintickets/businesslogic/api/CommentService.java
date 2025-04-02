package traintickets.businesslogic.api;

import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.TrainId;

import java.util.List;

public interface CommentService {
    void addComment(Comment comment);
    List<Comment> getComments(TrainId trainId);
    void deleteComment(CommentId commentId);
}
