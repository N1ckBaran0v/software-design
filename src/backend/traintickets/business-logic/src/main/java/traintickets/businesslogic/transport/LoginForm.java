package traintickets.businesslogic.transport;

import java.io.Serializable;

public record LoginForm(String username, String password) implements Serializable {
}
