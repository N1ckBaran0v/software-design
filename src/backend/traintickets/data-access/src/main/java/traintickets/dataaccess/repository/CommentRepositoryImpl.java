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
//    private final JdbcTemplate jdbcTemplate;
//
//    public CommentRepositoryImpl(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
//    }
//
//    @Override
//    public void addComment(String role, Comment comment) {
//        jdbcTemplate.executeCons(role, Connection.TRANSACTION_READ_COMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "INSERT INTO comments (user_id, train_id, score, comment_text) " +
//                            "VALUES (?, ?, ?, ?);"
//            )) {
//                statement.setLong(1, ((Number) comment.author().id()).longValue());
//                statement.setLong(2, ((Number) comment.train().id()).longValue());
//                statement.setInt(3, comment.score());
//                statement.setString(4, comment.text());
//                statement.execute();
//            }
//        });
//    }
//
//    @Override
//    public Iterable<Comment> getComments(String role, TrainId trainId) {
//        return jdbcTemplate.executeFunc(role, Connection.TRANSACTION_READ_COMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "SELECT * FROM comments WHERE train_id = (?);"
//            )) {
//                statement.setLong(1, (Long) trainId.id());
//                try (var resultSet = statement.executeQuery()) {
//                    var comments = new ArrayList<Comment>();
//                    var comment = getComment(resultSet);
//                    while (comment != null) {
//                        comments.add(comment);
//                        comment = getComment(resultSet);
//                    }
//                    return comments;
//                }
//            }
//        });
//    }
//
//    @Override
//    public void deleteComment(String role, CommentId commentId) {
//        jdbcTemplate.executeCons(role, Connection.TRANSACTION_READ_COMMITTED, connection -> {
//            try (var statement = connection.prepareStatement(
//                    "DELETE FROM comments WHERE id = (?);"
//            )) {
//                statement.setLong(1, ((Number) commentId.id()).longValue());
//                statement.execute();
//            }
//        });
//    }
//
//    private Comment getComment(ResultSet resultSet) throws SQLException {
//        var answer = (Comment) null;
//        if (resultSet.next()) {
//            var id = new CommentId(resultSet.getLong("id"));
//            var userId = new UserId(resultSet.getLong("user_id"));
//            var trainId = new TrainId(resultSet.getLong("train_id"));
//            var score = resultSet.getInt("score");
//            var comment = resultSet.getString("comment_text");
//            answer = new Comment(id, userId, trainId, score, comment);
//        }
//        return answer;
//    }
}
