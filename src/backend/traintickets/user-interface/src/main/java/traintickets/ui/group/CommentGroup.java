package traintickets.ui.group;

import traintickets.ui.controller.CommentController;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class CommentGroup extends AbstractEndpointGroup {
    private final CommentController commentController;
    private final SecurityConfiguration securityConfiguration;

    public CommentGroup(CommentController commentController, SecurityConfiguration securityConfiguration) {
        super("/trains/{trainId}/comments");
        this.commentController = Objects.requireNonNull(commentController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        post(ctx -> commentController.addComment(ctx, securityConfiguration.forUser(ctx)));
        get(ctx -> {
            securityConfiguration.forUser(ctx);
            commentController.getComments(ctx);
        });
        delete("/{commentId}", ctx -> {
            securityConfiguration.forAdmin(ctx);
            commentController.deleteComment(ctx);
        });
    }
}
