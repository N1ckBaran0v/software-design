package traintickets.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.jwt.JwtManager;
import traintickets.businesslogic.transport.UserInfo;
import traintickets.security.exception.InvalidTokenException;
import traintickets.security.exception.InvalidUserInfoException;

import java.util.*;

public final class JwtManagerImpl implements JwtManager {
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final int expiration;

    @SuppressWarnings("all")
    public JwtManagerImpl(JwtConfig jwtConfig) {
        algorithm = Algorithm.HMAC256(jwtConfig.secret());
        verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .withClaimPresence("id")
                .withClaimPresence("role")
                .build();
        expiration = jwtConfig.expiration();
    }

    @Override
    public String generateToken(UserId userId, String role) {
        if (userId == null || role == null) {
            throw new InvalidUserInfoException();
        }
        var version = getVersion(userId);
        return JWT.create()
                .withIssuer("auth0")
                .withClaim("id", userId.id())
                .withClaim("role", role)
                .withClaim("version", version)
                .withExpiresAt(getExpirationDate(expiration))
                .sign(algorithm);
    }

    private static Date getExpirationDate(int expiration) {
        var calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, expiration);
        return calendar.getTime();
    }

    @Override
    public UserInfo validateToken(String token) {
        try {
            var decoded = verifier.verify(token);
            var id = decoded.getClaim("id").asString();
            var role = decoded.getClaim("role").asString();
            var version = decoded.getClaim("version").asString();
            return new UserInfo(new UserId(id), role, version);
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException(e);
        }
    }

    @Override
    public void updateUser(UserId userId) {
        if (userId == null) {
            throw new InvalidUserInfoException();
        }
    }

    private String getVersion(UserId userId) {
        return null;
    }
}
