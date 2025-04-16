package traintickets.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;
import redis.clients.jedis.JedisPool;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.session.JwtManager;
import traintickets.businesslogic.transport.UserInfo;
import traintickets.security.exception.InvalidTokenException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public final class JwtManagerImpl implements JwtManager {
    private final JedisPool jedisPool;
    private final JWTProvider<UserInfo> jwtProvider;

    public JwtManagerImpl(JedisConfig jedisConfig, JwtConfig jwtConfig) {
        jedisPool = new JedisPool(jedisConfig.host(), jedisConfig.port(), jedisConfig.login(), jedisConfig.password());
        Runtime.getRuntime().addShutdownHook(new Thread(jedisPool::close));
        jwtProvider = getJwtProvider(jwtConfig);
    }

    static JWTProvider<UserInfo> getJwtProvider(JwtConfig jwtConfig) {
        var algorithm = Algorithm.HMAC256(jwtConfig.secret());
        var generator = (JWTGenerator<UserInfo>) (userInfo, algoritm) ->
                JWT.create()
                        .withClaim("id", userInfo.userId().id())
                        .withClaim("role", userInfo.role())
                        .withExpiresAt(getExpirationDate(jwtConfig.expiration()))
                        .sign(algorithm);
        var verifier = JWT.require(algorithm).build();
        return new JWTProvider<>(algorithm, generator, verifier);
    }

    private static Date getExpirationDate(int expiration) {
        var calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, expiration);
        return calendar.getTime();
    }

    @Override
    public String generateToken(UserInfo userInfo) {
        try (var connection = jedisPool.getResource()) {
            var userId = userInfo.userId().id();
            var token = jwtProvider.generateToken(userInfo);
            var tokenKey = String.format("token:%s", token);
            var userKey = String.format("user:%s", userId);
            connection.hset(tokenKey, userKey, "");
            var sessions = new HashMap<>(connection.hgetAll(userKey));
            sessions.put(tokenKey, "");
            connection.hset(userKey, sessions);
            return token;
        }
    }

    @Override
    public UserInfo validateToken(String token) {
        try (var connection = jedisPool.getResource()) {
            var tokenKey = String.format("token:%s", token);
            if (!connection.exists(tokenKey)) {
                throw new InvalidTokenException(token);
            }
            var decoded = jwtProvider.validateToken(token).orElseThrow(() -> new InvalidTokenException(token));
            var userId = new UserId(decoded.getClaim("id").asString());
            var role = decoded.getClaim("role").asString();
            return new UserInfo(userId, role);
        }
    }

    @Override
    public void invalidateToken(String token) {
        try (var connection = jedisPool.getResource()) {
            var decoded = jwtProvider.validateToken(token).orElseThrow(() -> new InvalidTokenException(token));
            var userId = decoded.getClaim("id").asString();
            var tokenKey = String.format("token:%s", token);
            connection.del(tokenKey);
            var userKey = String.format("user:%s", userId);
            var map = connection.hgetAll(userKey);
            if (map.size() > 1) {
                connection.hdel(userKey, tokenKey);
            } else {
                connection.del(userKey);
            }
        }
    }

    @Override
    public void invalidateTokens(UserId userId) {
        try (var connection = jedisPool.getResource()) {
            var userKey = String.format("user:%s", userId.id());
            var map = connection.hgetAll(userKey);
            for (var tokenKey : map.keySet()) {
                connection.del(tokenKey);
            }
            connection.del(userKey);
        }
    }
}
