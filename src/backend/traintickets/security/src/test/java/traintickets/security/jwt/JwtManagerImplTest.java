package traintickets.security.jwt;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.JedisPool;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.session.JwtManager;
import traintickets.businesslogic.transport.UserInfo;
import traintickets.security.exception.InvalidTokenException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtManagerImplTest {
    private JwtManager jwtManager;
    private JedisConfig jedisConfig;
    private JwtConfig jwtConfig;
    private RedisContainer redisContainer;
    private JedisPool jedisPool;
    private List<String> tokens;

    @BeforeEach
    void setUp() {
        redisContainer = new RedisContainer(DockerImageName.parse("redis:7.0.15"));
        redisContainer.start();
        jedisConfig = new JedisConfig(redisContainer.getRedisHost(), redisContainer.getRedisPort(), null, null);
        jedisPool = new JedisPool(jedisConfig.host(), jedisConfig.port(), jedisConfig.login(), jedisConfig.password());
        jwtConfig = new JwtConfig("Tralalelo Tralala", 2);
        jwtManager = new JwtManagerImpl(jedisConfig, jwtConfig);
        insertData();
    }

    void insertData() {
        var provider1 = JwtManagerImpl.getJwtProvider(jwtConfig);
        var provider2 = JwtManagerImpl.getJwtProvider(new JwtConfig("Tralalelo Tralala", 0));
        var userInfo1 = new UserInfo(new UserId("1"), "user_role");
        var userInfo2 = new UserInfo(new UserId("2"), "user_role");
        var token1 = provider1.generateToken(userInfo1);
        var token2 = provider2.generateToken(userInfo1);
        var token3 = provider1.generateToken(userInfo2);
        try (var connection = jedisPool.getResource()) {
            var user1 = String.format("user:%s", userInfo1.userId().id());
            var user2 = String.format("user:%s", userInfo2.userId().id());
            var key1 = String.format("token:%s", token1);
            var key2 = String.format("token:%s", token2);
            var key3 = String.format("token:%s", token3);
            connection.hset(key1, user1, "");
            connection.hset(key2, user1, "");
            connection.hset(key3, user2, "");
            connection.hset(user1, Map.of(key1, "", key2, ""));
            connection.hset(user2, Map.of(key3, ""));
        }
        tokens = List.of(token1, token2, token3);
    }

    @Test
    void generateToken_positive_oldUser() {
        var userInfo = new UserInfo(new UserId("1"), "user_role");
        var token = jwtManager.generateToken(userInfo);
        assertNotNull(token);
        try (var connection = jedisPool.getResource()) {
            var tokenKey = String.format("token:%s", token);
            var userKey = String.format("user:%s", userInfo.userId().id());
            assertTrue(connection.exists(tokenKey));
            assertTrue(connection.exists(userKey));
            assertTrue(connection.hgetAll(userKey).size() > 1);
        }
    }

    @Test
    void generateToken_positive_newUser() {
        var userInfo = new UserInfo(new UserId("3"), "user_role");
        var token = jwtManager.generateToken(userInfo);
        assertNotNull(token);
        try (var connection = jedisPool.getResource()) {
            var tokenKey = String.format("token:%s", token);
            var userKey = String.format("user:%s", userInfo.userId().id());
            assertTrue(connection.exists(tokenKey));
            assertTrue(connection.exists(userKey));
            assertEquals(1, connection.hgetAll(userKey).size());
        }
    }

    @Test
    void validateToken_positive_validated() {
        var userInfo = new UserInfo(new UserId("1"), "user_role");
        var result = jwtManager.validateToken(tokens.getFirst());
        assertNotNull(result);
        assertEquals(userInfo, result);
    }

    @Test
    void validateToken_negative_expired() {
        assertThrows(InvalidTokenException.class, () -> jwtManager.validateToken(tokens.get(1)));
    }

    @Test
    void validateToken_negative_notFound() {
        var provider = JwtManagerImpl.getJwtProvider(new JwtConfig("Tralalelo Tralala", 4));
        var token = provider.generateToken(new UserInfo(new UserId("1"), "user_role"));
        System.out.println(token);
        System.out.println(tokens.getFirst());
        assertThrows(InvalidTokenException.class, () -> jwtManager.validateToken(token));
    }

    @Test
    void validateToken_negative_invalidToken() {
        assertThrows(InvalidTokenException.class, () -> jwtManager.validateToken("Bombardiro Crocodilo"));
    }

    @Test
    void invalidateToken_positive_last() {
        var token = tokens.getLast();
        jwtManager.invalidateToken(token);
        try (var connection = jedisPool.getResource()) {
            var tokenKey = String.format("token:%s", token);
            var provider = JwtManagerImpl.getJwtProvider(jwtConfig);
            var decoded = provider.validateToken(tokens.getLast()).orElseThrow(() -> new InvalidTokenException(token));
            var userId = decoded.getClaim("id").asString();
            var userKey = String.format("user:%s", userId);
            assertFalse(connection.exists(tokenKey));
            assertFalse(connection.exists(userKey));
        }
    }

    @Test
    void invalidateToken_positive_notLast() {
        var token = tokens.getFirst();
        jwtManager.invalidateToken(token);
        try (var connection = jedisPool.getResource()) {
            var tokenKey = String.format("token:%s", token);
            var provider = JwtManagerImpl.getJwtProvider(jwtConfig);
            var decoded = provider.validateToken(tokens.getLast()).orElseThrow(() -> new InvalidTokenException(token));
            var userId = decoded.getClaim("id").asString();
            var userKey = String.format("user:%s", userId);
            assertFalse(connection.exists(tokenKey));
            assertTrue(connection.exists(userKey));
            assertEquals(1, connection.hgetAll(userKey).size());
        }
    }

    @Test
    void invalidateTokens_positive_last() {
        var userId = new UserId("2");
        jwtManager.invalidateTokens(userId);
        try (var connection = jedisPool.getResource()) {
            var tokenKey = String.format("token:%s", tokens.getLast());
            var userKey = String.format("user:%s", userId.id());
            assertFalse(connection.exists(tokenKey));
            assertFalse(connection.exists(userKey));
        }
    }

    @Test
    void invalidateTokens_positive_notLast() {
        var userId = new UserId("1");
        jwtManager.invalidateTokens(userId);
        try (var connection = jedisPool.getResource()) {
            var tokenKey1 = String.format("token:%s", tokens.get(0));
            var tokenKey2 = String.format("token:%s", tokens.get(1));
            var userKey = String.format("user:%s", userId.id());
            assertFalse(connection.exists(tokenKey1));
            assertFalse(connection.exists(tokenKey2));
            assertFalse(connection.exists(userKey));
        }
    }

    @AfterEach
    void tearDown() {
        redisContainer.stop();
        redisContainer.close();
        jedisPool.close();
    }
}