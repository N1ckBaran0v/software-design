package traintickets.security.jwt;

public record JwtConfig(String secret, int expiration) {
}
