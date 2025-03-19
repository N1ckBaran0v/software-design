package traintickets.businesslogic.repository;

import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.TrainId;

public interface CommentRepository {
    void addComment(Comment comment);
    Iterable<Comment> getComments(TrainId trainId);
    void deleteComment(CommentId commentId);
}
