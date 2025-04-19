package traintickets.ui.group;

import traintickets.ui.controller.CommentController;
import traintickets.ui.security.SecurityConfiguration;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public final class CommentGroup extends AbstractEndpointGroup {
    private final CommentController commentController;
    private final SecurityConfiguration securityConfiguration;

    public CommentGroup(CommentController commentController, SecurityConfiguration securityConfiguration) {
        super("/comments");
        this.commentController = Objects.requireNonNull(commentController);
        this.securityConfiguration = Objects.requireNonNull(securityConfiguration);
    }

    @Override
    public void addEndpoints() {
        post(ctx -> commentController.addComment(ctx, securityConfiguration.forUser(ctx)));
        get(ctx -> commentController.getComments(ctx, securityConfiguration.authorizedOnly(ctx)));
        delete("/{commentId}", ctx -> commentController.deleteComment(ctx, securityConfiguration.forAdmin(ctx)));
    }
}
