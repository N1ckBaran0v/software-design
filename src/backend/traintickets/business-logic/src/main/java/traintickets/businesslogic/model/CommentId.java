package traintickets.businesslogic.model;

import java.io.Serializable;
import java.util.Objects;

public record CommentId(String id) implements Serializable {
    public CommentId(String id) {
        this.id = Objects.requireNonNull(id);
    }
}
