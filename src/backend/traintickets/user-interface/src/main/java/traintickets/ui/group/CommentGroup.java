package traintickets.ui.group;

import traintickets.ui.controller.CommentController;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class CommentGroup extends AbstractEndpointGroup {
    private final CommentController commentController;

    public CommentGroup(CommentController commentController) {
        super("/api/comments");
        this.commentController = Objects.requireNonNull(commentController);
    }

    @Override
    public void addEndpoints() {
        post(commentController::addComment);
        get(commentController::getComments);
        delete("/{commentId}", commentController::deleteComment);
    }
}
