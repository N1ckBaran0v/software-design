package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.repository.CommentRepository;
import traintickets.dataaccess.mongo.connection.MongoExecutor;
import traintickets.dataaccess.mongo.model.CommentDocument;

import java.util.stream.StreamSupport;

public final class CommentRepositoryImpl implements CommentRepository {
    private final MongoExecutor mongoExecutor;
    private final MongoCollection<CommentDocument> commentsCollection;

    public CommentRepositoryImpl(MongoExecutor mongoExecutor) {
        this.mongoExecutor = mongoExecutor;
        commentsCollection = mongoExecutor.getDatabase().getCollection("comments", CommentDocument.class);
    }

    @Override
    public void addComment(Comment comment) {
        mongoExecutor.executeConsumer(session -> commentsCollection.insertOne(new CommentDocument(comment)));
    }

    @Override
    public Iterable<Comment> getComments(TrainId trainId) {
        return mongoExecutor.executeFunction(session -> StreamSupport.stream(
                commentsCollection.find(session, Filters.eq("train", new ObjectId(trainId.id()))).spliterator(), false)
                .map(CommentDocument::toComment).toList());
    }

    @Override
    public void deleteComment(CommentId commentId) {
        mongoExecutor.executeConsumer(session ->
                commentsCollection.deleteOne(Filters.eq("_id", new ObjectId(commentId.id()))));
    }
}
