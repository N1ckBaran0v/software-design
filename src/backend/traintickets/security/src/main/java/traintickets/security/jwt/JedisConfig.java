package traintickets.security.jwt;

public record JedisConfig(String host, int port, String login, String password) {
}
