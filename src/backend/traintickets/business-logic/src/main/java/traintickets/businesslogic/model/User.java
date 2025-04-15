package traintickets.businesslogic.model;

import traintickets.businesslogic.exception.InvalidEntityException;

import java.io.Serializable;
import java.util.regex.Pattern;

public record User(UserId id,
                   String username,
                   String password,
                   String name,
                   String role,
                   boolean active) implements Serializable {
    private static final Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9_-]{3,15}$");
    private static final Pattern passwordPattern = Pattern.compile("^.{8,20}$");
    private static final Pattern namePattern = Pattern.compile("^.{3,30}$");

    public void validate() {
        if (username == null || password == null || name == null || role == null) {
            throw new InvalidEntityException("All data required");
        }
        if (!usernamePattern.matcher(username).matches()) {
            throw new InvalidEntityException("Invalid username");
        }
        if (!passwordPattern.matcher(password).matches()) {
            throw new InvalidEntityException("Invalid password");
        }
        if (!namePattern.matcher(name).matches()) {
            throw new InvalidEntityException("Invalid name");
        }
    }
}
