package traintickets.control.configuration;

import java.util.Map;

public final class SecurityConfig {
    private Map<String, String> roles;

    public Map<String, String> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, String> roles) {
        this.roles = roles;
    }
}
