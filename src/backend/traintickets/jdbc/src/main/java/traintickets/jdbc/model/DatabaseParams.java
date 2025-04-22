package traintickets.jdbc.model;

import java.util.Map;

public record DatabaseParams(String url, String username, String password, Map<String, String> roles, int poolSize) {
}
