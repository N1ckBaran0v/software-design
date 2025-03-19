package traintickets.dataaccess.repository;

import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.CommentRepository;

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
