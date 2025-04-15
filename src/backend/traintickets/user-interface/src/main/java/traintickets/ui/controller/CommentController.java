package traintickets.ui.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import traintickets.businesslogic.api.CommentService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.TrainId;
import traintickets.ui.exception.QueryParameterNotFoundException;

import java.util.Objects;

public final class CommentController {
    private final CommentService commentService;
    private final UniLogger logger;

    public CommentController(CommentService commentService, UniLoggerFactory loggerFactory) {
        this.commentService = Objects.requireNonNull(commentService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(CommentController.class);
    }

    public void addComment(Context ctx) {
        var comment = ctx.bodyAsClass(Comment.class);
        logger.debug("comment: %s", comment);
        commentService.addComment(ctx.cookie("sessionId"), comment);
        ctx.status(HttpStatus.CREATED);
        logger.debug("comment added");
    }

    public void getComments(Context ctx) {
        var trainId = ctx.queryParam("trainId");
        logger.debug("trainId: %s", trainId);
        if (trainId == null) {
            throw new QueryParameterNotFoundException("trainId");
        }
        commentService.getComments(ctx.cookie("sessionId"), new TrainId(trainId));
        logger.debug("comments got");
    }

    public void deleteComment(Context ctx) {
        var commentId = ctx.pathParam("commentId");
        logger.debug("commentId: %s", commentId);
        commentService.deleteComment(ctx.cookie("sessionId"), new CommentId(ctx.pathParam("commentId")));
        ctx.status(HttpStatus.NO_CONTENT);
        logger.debug("comment deleted");
    }
}
