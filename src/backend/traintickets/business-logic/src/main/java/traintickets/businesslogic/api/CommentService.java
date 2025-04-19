package traintickets.businesslogic.api;

import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.transport.UserInfo;

import java.util.List;

public interface CommentService {
    void addComment(UserInfo userInfo, Comment comment);
    List<Comment> getComments(UserInfo userInfo, TrainId trainId);
    void deleteComment(UserInfo userInfo, CommentId commentId);
}
