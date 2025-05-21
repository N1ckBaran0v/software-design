package traintickets.dataaccess.mongo.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.model.UserId;

public record CommentDocument(@BsonId ObjectId id, ObjectId author, ObjectId train, int score, String text) {
    public CommentDocument(Comment comment) {
        this(
                comment.id() == null ? null : new ObjectId(comment.id().id()),
                new ObjectId(comment.author().id()),
                new ObjectId(comment.train().id()),
                comment.score(),
                comment.text()
        );
    }

    public Comment toComment() {
        return new Comment(
                id == null ? null : new CommentId(id.toHexString()),
                new UserId(author.toHexString()),
                new TrainId(train.toHexString()),
                score,
                text
        );
    }
}
