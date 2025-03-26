package traintickets.dataaccess.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.CommentRepository;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class CommentRepositoryImplIT extends PostgresIT {
    private CommentRepository commentRepository;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        commentRepository = new CommentRepositoryImpl(jdbcTemplate, roleName);
    }

    @Test
    void addComment_positive_added() {
        var comment = new Comment(null, new UserId(1), new TrainId(1), 4, "Упс, не туда нажал");
        commentRepository.addComment(comment);
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, conn -> {
            try (var statement = conn.prepareStatement("select * from comments where id = 3;")) {
                try (var resultSet = statement.executeQuery()) {
                    assertTrue(resultSet.next());
                    assertEquals(comment.author().id(), resultSet.getLong(2));
                    assertEquals(comment.train().id(), resultSet.getLong(3));
                    assertEquals(comment.score(), resultSet.getInt(4));
                    assertEquals(comment.text(), resultSet.getString(5));
                    assertFalse(resultSet.next());
                }
            }
        });
    }

    @Test
    void getComments_positive_got() {
        var trainId = new TrainId(1);
        var comment1 = new Comment(new CommentId(1), new UserId(1), trainId, 5, "Лучший поезд");
        var comment2 = new Comment(new CommentId(2), new UserId(2), trainId, 1, "Грубые проводники");
        var result = commentRepository.getComments(trainId);
        assertNotNull(result);
        var iterator = result.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(comment1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(comment2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void getComments_positive_empty() {
        var result = commentRepository.getComments(new TrainId(3));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void deleteComment() {
        var commentId = new CommentId(1);
        commentRepository.deleteComment(commentId);
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, conn -> {
            try (var statement = conn.prepareStatement("select * from comments where id = 1;")) {
                try (var resultSet = statement.executeQuery()) {
                    assertFalse(resultSet.next());
                }
            }
        });
    }
}