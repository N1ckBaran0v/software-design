package traintickets.ui.controller;

import io.javalin.http.Context;
import traintickets.businesslogic.api.CommentService;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.TrainId;
import traintickets.ui.exception.QueryParameterNotFoundException;

import java.util.Objects;

public final class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = Objects.requireNonNull(commentService);
    }

    public void addComment(Context ctx) {
        commentService.addComment(ctx.cookie("sessionId"), ctx.bodyAsClass(Comment.class));
    }

    public void getComments(Context ctx) {
        var trainId = ctx.queryParam("trainId");
        if (trainId == null) {
            throw new QueryParameterNotFoundException("trainId");
        }
        commentService.getComments(ctx.cookie("sessionId"), new TrainId(trainId));
    }

    public void deleteComment(Context ctx) {
        commentService.deleteComment(ctx.cookie("sessionId"), new CommentId(ctx.pathParam("commentId")));
    }
}
