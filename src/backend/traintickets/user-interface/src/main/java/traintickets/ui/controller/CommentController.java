package traintickets.ui.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import traintickets.businesslogic.api.CommentService;
import traintickets.businesslogic.logger.UniLogger;
import traintickets.businesslogic.logger.UniLoggerFactory;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.transport.UserInfo;
import traintickets.ui.exception.QueryParameterNotFoundException;

import java.util.Objects;

public final class CommentController {
    private final CommentService commentService;
    private final UniLogger logger;

    public CommentController(CommentService commentService, UniLoggerFactory loggerFactory) {
        this.commentService = Objects.requireNonNull(commentService);
        this.logger = Objects.requireNonNull(loggerFactory).getLogger(CommentController.class);
    }

    public void addComment(Context ctx, UserInfo userInfo) {
        var comment = ctx.bodyAsClass(Comment.class);
        logger.debug("comment: %s", comment);
        commentService.addComment(userInfo, comment);
        ctx.status(HttpStatus.CREATED);
        logger.debug("comment added");
    }

    public void getComments(Context ctx, UserInfo userInfo) {
        var trainId = ctx.pathParam("trainId");
        logger.debug("trainId: %s", trainId);
        ctx.json(commentService.getComments(userInfo, new TrainId(trainId)));
        logger.debug("comments got");
    }

    public void deleteComment(Context ctx, UserInfo userInfo) {
        var commentId = ctx.pathParam("commentId");
        logger.debug("commentId: %s", commentId);
        commentService.deleteComment(userInfo, new CommentId(ctx.pathParam("commentId")));
        ctx.status(HttpStatus.NO_CONTENT);
        logger.debug("comment deleted");
    }
}
