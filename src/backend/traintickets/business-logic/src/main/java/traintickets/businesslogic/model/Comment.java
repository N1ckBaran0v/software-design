package traintickets.businesslogic.model;

import traintickets.businesslogic.exception.InvalidEntityException;

import java.io.Serializable;

public record Comment(CommentId id, UserId author, TrainId train, int score, String text) implements Serializable {
    public void validate() {
        if (author == null || train == null || score < 1 || score > 5 || text == null) {
            throw new InvalidEntityException("Invalid comment");
        }
    }
}
