package traintickets.jdbc.model;

public record DatabaseParams(String url, String username, String password, int poolSize) {
}
