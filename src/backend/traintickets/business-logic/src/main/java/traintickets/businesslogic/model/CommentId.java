package traintickets.businesslogic.model;

import java.util.Objects;

public record CommentId(String id) {
    public CommentId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
