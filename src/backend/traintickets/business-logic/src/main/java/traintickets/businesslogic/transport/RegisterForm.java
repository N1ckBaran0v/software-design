package traintickets.businesslogic.transport;

import java.io.Serializable;

public record RegisterForm(String username,
                           String password,
                           String confirmPassword,
                           String name) implements Serializable {
}
