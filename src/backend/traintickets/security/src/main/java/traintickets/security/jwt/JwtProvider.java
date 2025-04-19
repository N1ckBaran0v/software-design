package traintickets.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.transport.UserInfo;
import traintickets.security.exception.InvalidTokenException;
import traintickets.security.exception.InvalidUserInfoException;

import java.util.Calendar;
import java.util.Date;

public final class JwtProvider {
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final int expiration;


    public JwtProvider(JwtConfig jwtConfig) {
        algorithm = Algorithm.HMAC256(jwtConfig.secret());
        verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .withClaimPresence("id")
                .withClaimPresence("role")
                .build();
        expiration = jwtConfig.expiration();
    }

    public String generateToken(UserInfo userInfo) {
        if (userInfo == null) {
            throw new InvalidUserInfoException();
        }
        if (userInfo.userId() == null || userInfo.role() == null) {
            throw new InvalidUserInfoException(userInfo);
        }
        return JWT.create()
                .withIssuer("auth0")
                .withClaim("id", userInfo.userId().id())
                .withClaim("role", userInfo.role())
                .withExpiresAt(getExpirationDate(expiration))
                .sign(algorithm);
    }

    private static Date getExpirationDate(int expiration) {
        var calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, expiration);
        return calendar.getTime();
    }

    public UserInfo validateToken(String token) {
        try {
            var decoded = verifier.verify(token);
            var id = decoded.getClaim("id").asString();
            var role = decoded.getClaim("role").asString();
            return new UserInfo(new UserId(id), role);
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException(e);
        }
    }
}
