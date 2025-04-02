package traintickets.businesslogic.model;

import traintickets.businesslogic.exception.InvalidEntityException;

public record Comment(CommentId id, UserId author, TrainId train, int score, String text) {
    public void validate() {
        if (author == null || train == null || score < 0 || score > 5 || text == null) {
            throw new InvalidEntityException("Invalid comment");
        }
    }
}
