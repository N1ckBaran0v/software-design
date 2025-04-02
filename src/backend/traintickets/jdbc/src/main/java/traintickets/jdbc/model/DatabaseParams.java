package traintickets.jdbc.model;

import java.util.Map;

public record DatabaseParams(String url, Map<String, String> users, int poolSize) {
}
