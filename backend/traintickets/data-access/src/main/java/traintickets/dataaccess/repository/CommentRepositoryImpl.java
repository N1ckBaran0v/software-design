package traintickets.dataaccess.repository;

import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.CommentRepository;
import traintickets.jdbc.api.JdbcTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public final class CommentRepositoryImpl implements CommentRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String userRoleName;

    public CommentRepositoryImpl(JdbcTemplate jdbcTemplate, String userRoleName) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.userRoleName = Objects.requireNonNull(userRoleName);
    }

    @Override
    public void addComment(Comment comment) {
        jdbcTemplate.executeCons(userRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            try (var statement = conn.prepareStatement(
                    "INSERT INTO comments (user_id, train_id, score, comment_text) " +
                            "VALUES (?, ?, ?, ?);"
            )) {
                statement.setLong(1, comment.author().id());
                statement.setLong(2, comment.train().id());
                statement.setInt(3, comment.score());
                statement.setString(4, comment.text());
                statement.execute();
            }
        });
    }

    @Override
    public Iterable<Comment> getComments(TrainId trainId) {
        return jdbcTemplate.executeFunc(userRoleName, Connection.TRANSACTION_READ_COMMITTED, conn -> {
            try (var statement = conn.prepareStatement(
                    "SELECT * FROM comments WHERE train_id = (?);"
            )) {
                statement.setLong(1, trainId.id());
                try (var resultSet = statement.executeQuery()) {
                    var comments = new ArrayList<Comment>();
                    var comment = getComment(resultSet);
                    while (comment != null) {
                        comments.add(comment);
                        comment = getComment(resultSet);
                    }
                    return comments;
                }
            }
        });
    }

    @Override
    public void deleteComment(CommentId commentId) {
        jdbcTemplate.executeCons(userRoleName, Connection.TRANSACTION_REPEATABLE_READ, conn -> {
            try (var statement = conn.prepareStatement(
                    "DELETE FROM comments WHERE id = (?);"
            )) {
                statement.setLong(1, commentId.id());
                statement.execute();
            }
        });
    }

    private Comment getComment(ResultSet resultSet) throws SQLException {
        var answer = (Comment) null;
        if (resultSet.next()) {
            var id = new CommentId(resultSet.getLong("id"));
            var userId = new UserId(resultSet.getLong("user_id"));
            var trainId = new TrainId(resultSet.getLong("train_id"));
            var score = resultSet.getInt("score");
            var comment = resultSet.getString("comment_text");
            answer = new Comment(id, userId, trainId, score, comment);
        }
        return answer;
    }
}
