package traintickets.ui.group;

import io.javalin.http.HandlerType;
import traintickets.ui.controller.CommentController;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class CommentGroup extends AbstractEndpointGroup {
    private final CommentController commentController;
    private final SecurityConfiguration securityConfiguration;

    public CommentGroup(CommentController commentController, SecurityConfiguration securityConfiguration) {
        super("/api/comments");
        this.commentController = Objects.requireNonNull(commentController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        before(ctx -> {
            if (ctx.method().equals(HandlerType.POST)) {
                securityConfiguration.forUser(ctx);
            } else if (ctx.method().equals(HandlerType.DELETE)) {
                securityConfiguration.forAdmin(ctx);
            }
        });
        post(commentController::addComment);
        get(commentController::getComments);
        delete("/{commentId}", commentController::deleteComment);
    }
}
