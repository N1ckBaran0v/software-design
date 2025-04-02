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

    @Override
    protected void insertData() {
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement(
                    "insert into users_view (user_name, pass_word, real_name, user_role, is_active) values " +
                            "('first', 'qwerty123', 'Иванов Иван Иванович', 'userRole', TRUE), " +
                            "('second', 'qwerty123', 'Петров Пётр Петрович', 'userRole', TRUE); " +
                            "insert into trains (train_class) values ('Скорый'); " +
                            "insert into comments (user_id, train_id, score, comment_text) values " +
                            "(1, 1, 5, 'Лучший поезд'), " +
                            "(2, 1, 1, 'Грубые проводники'); "
            )) {
                statement.execute();
            }
        });
    }

    @Test
    void addComment_positive_added() {
        var comment = new Comment(null, new UserId(1L), new TrainId(1L), 4, "Упс, не туда нажал");
        commentRepository.addComment(comment);
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement("select * from comments where id = 3;")) {
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
        var trainId = new TrainId(1L);
        var comment1 = new Comment(new CommentId(1L), new UserId(1L), trainId, 5, "Лучший поезд");
        var comment2 = new Comment(new CommentId(2L), new UserId(2L), trainId, 1, "Грубые проводники");
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
        var result = commentRepository.getComments(new TrainId(3L));
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void deleteComment_positive_deleted() {
        var commentId = new CommentId(1L);
        commentRepository.deleteComment(commentId);
        jdbcTemplate.executeCons(roleName, Connection.TRANSACTION_READ_UNCOMMITTED, connection -> {
            try (var statement = connection.prepareStatement("select * from comments where id = 1;")) {
                try (var resultSet = statement.executeQuery()) {
                    assertFalse(resultSet.next());
                }
            }
        });
    }
}