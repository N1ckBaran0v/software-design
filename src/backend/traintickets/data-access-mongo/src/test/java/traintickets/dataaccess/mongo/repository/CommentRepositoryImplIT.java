package traintickets.dataaccess.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.CommentRepository;
import traintickets.dataaccess.mongo.model.CommentDocument;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class CommentRepositoryImplIT extends MongoIT {
    private CommentRepository commentRepository;
    private MongoCollection<CommentDocument> commentCollection;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        commentRepository = new CommentRepositoryImpl(mongoExecutor);
    }

    @AfterEach
    @Override
    void tearDown() {
        super.tearDown();
    }

    @Override
    protected void insertData() {
        commentCollection = mongoExecutor.getDatabase().getCollection("comments", CommentDocument.class);
        var trainId = new ObjectId("123456789012345678901234");
        mongoExecutor.executeConsumer(session -> commentCollection.insertMany(session, List.of(
                new CommentDocument(null, new ObjectId("111122223333444455556666"), trainId, 5, "Лучший поезд"),
                new CommentDocument(null, new ObjectId("999988887777666655554444"), trainId, 1, "Грубые проводники")
        )));
    }

    @Test
    void addComment_positive_added() {
        var userId = new UserId("123456789012345678901234");
        var trainId = new TrainId("123456789012345678901234");
        var comment = new Comment(null, userId, trainId, 4, "Упс, не туда нажал");
        commentRepository.addComment(comment);
        mongoExecutor.executeConsumer(session -> assertEquals(3, commentCollection.countDocuments(session)));
    }

    @Test
    void getComments_positive_got() {
        var commentId1 = mongoExecutor.executeFunction(session -> new CommentId(Objects.requireNonNull(
                commentCollection.find(session, Filters.eq("score", 5)).first()).id().toHexString()));
        var commentId2 = mongoExecutor.executeFunction(session -> new CommentId(Objects.requireNonNull(
                commentCollection.find(session, Filters.eq("score", 1)).first()).id().toHexString()));
        var trainId = new TrainId("123456789012345678901234");
        var comment1 = new Comment(commentId1, new UserId("111122223333444455556666"), trainId, 5, "Лучший поезд");
        var comment2 = new Comment(commentId2, new UserId("999988887777666655554444"), trainId, 1, "Грубые проводники");
        var result = commentRepository.getComments(trainId);
        assertNotNull(result);
        var list = StreamSupport.stream(result.spliterator(), false).toList();
        assertEquals(2, list.size());
        assertTrue(comment1.equals(list.get(0)) || comment1.equals(list.get(1)));
        assertTrue(comment2.equals(list.get(0)) || comment2.equals(list.get(1)));
    }

    @Test
    void getComments_positive_empty() {
        var result = commentRepository.getComments(new TrainId("123456789098765432123456"));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void deleteComment_positive_deleted() {
        var commentId = mongoExecutor.executeFunction(session -> new CommentId(Objects.requireNonNull(
                commentCollection.find(session, Filters.eq("score", 5)).first()).id().toHexString()));
        commentRepository.deleteComment(commentId);
        mongoExecutor.executeConsumer(session -> assertEquals(1, commentCollection.countDocuments(session)));
    }
}