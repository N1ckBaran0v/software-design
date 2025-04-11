package traintickets.security.session;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.JedisPool;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.session.SessionManager;
import traintickets.businesslogic.transport.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerImplTest {
    private SessionManager sessionManager;
    private RedisContainer redisContainer;
    private JedisPool jedisPool;
    private List<String> uuids;

    @BeforeEach
    void setUp() {
        redisContainer = new RedisContainer(DockerImageName.parse("redis:7.0.15"));
        redisContainer.start();
        sessionManager =
                new SessionManagerImpl(redisContainer.getRedisHost(), redisContainer.getRedisPort(), null, null);
        insertData();
    }

    void insertData() {
        uuids = new ArrayList<>();
        uuids.add(UUID.randomUUID().toString());
        uuids.add(UUID.randomUUID().toString());
        uuids.add(UUID.randomUUID().toString());
        var userId = "1";
        var role = "user_role";
        jedisPool = new JedisPool(redisContainer.getRedisHost(), redisContainer.getRedisPort());
        try (var connection = jedisPool.getResource()) {
            var hash = new HashMap<String, String>();
            hash.put("id", userId);
            hash.put("role", role);
            var sessionKey1 = String.format("session:%s", uuids.get(0));
            connection.hset(sessionKey1, hash);
            var sessionKey2 = String.format("session:%s", uuids.get(1));
            var sessionKey3 = String.format("session:%s", uuids.get(2));
            connection.hset(sessionKey3, hash);
            var userHash = new HashMap<String, String>();
            userHash.put(sessionKey1, "");
            userHash.put(sessionKey2, "");
            userHash.put(sessionKey3, "");
            var userKey = String.format("user:%s", userId);
            connection.hset(userKey, userHash);
        }
    }

    @Test
    void startSession_positive_startedUnknown() {
        var sessionId = sessionManager.generateSessionId();
        var user = new User(new UserId(2), "random_username", "qwerty123", "Zubenko Mikhail", "user_role", true);
        sessionManager.startSession(sessionId, user);
        try (var connection = jedisPool.getResource()) {
            var sessionKey = String.format("session:%s", sessionId);
            var hash = connection.hgetAll(sessionKey);
            assertNotNull(hash);
            assertEquals(2, hash.size());
            assertTrue(hash.containsKey("id"));
            assertEquals(user.id(), new UserId(hash.get("id")));
            assertTrue(hash.containsKey("role"));
            assertEquals(user.role(), hash.get("role"));
            var userKey = String.format("user:%s", user.id().id());
            var sessions = connection.hgetAll(userKey);
            assertNotNull(sessions);
            assertEquals(1, sessions.size());
            assertTrue(sessions.containsKey(sessionKey));
        }
    }

    @Test
    void startSession_positive_startedKnown() {
        var sessionId = sessionManager.generateSessionId();
        var user = new User(new UserId(1), "random_username", "qwerty123", "Zubenko Mikhail", "user_role", true);
        sessionManager.startSession(sessionId, user);
        try (var connection = jedisPool.getResource()) {
            var sessionKey = String.format("session:%s", sessionId);
            var hash = connection.hgetAll(sessionKey);
            assertNotNull(hash);
            assertEquals(2, hash.size());
            assertTrue(hash.containsKey("id"));
            assertEquals(user.id(), new UserId(hash.get("id")));
            assertTrue(hash.containsKey("role"));
            assertEquals(user.role(), hash.get("role"));
            var userKey = String.format("user:%s", user.id().id());
            var sessions = connection.hgetAll(userKey);
            assertNotNull(sessions);
            assertEquals(4, sessions.size());
            assertTrue(sessions.containsKey(sessionKey));
            for (var anotherId : uuids) {
                assertTrue(sessions.containsKey(String.format("session:%s", anotherId)));
            }
        }
    }

    @Test
    void getUserInfo_positive_found() {
        var sessionId = uuids.getFirst();
        var userInfo = sessionManager.getUserInfo(sessionId);
        assertNotNull(userInfo);
        assertEquals(new UserId(1), userInfo.userId());
        assertEquals("user_role", userInfo.role());
    }

    @Test
    void getUserInfo_positive_notFound() {
        var sessionId = uuids.get(1);
        var userInfo = sessionManager.getUserInfo(sessionId);
        assertNotNull(userInfo);
        assertNull(userInfo.userId());
        assertNull(userInfo.role());
    }

    @Test
    void updateUserInfo_positive_updated() {
        var userInfo = new UserInfo(new UserId(1), "admin_role");
        sessionManager.updateUserInfo(userInfo);
        try (var connection = jedisPool.getResource()) {
            var userKey = String.format("user:%s", userInfo.userId().id());
            var hash = connection.hgetAll(userKey);
            assertNotNull(hash);
            assertEquals(2, hash.size());
            var sessionKey1 = String.format("session:%s", uuids.get(0));
            assertTrue(hash.containsKey(sessionKey1));
            var sessionKey2 = String.format("session:%s", uuids.get(1));
            assertFalse(hash.containsKey(sessionKey2));
            var sessionKey3 = String.format("session:%s", uuids.get(2));
            assertTrue(hash.containsKey(sessionKey3));
            var hash1 = connection.hgetAll(sessionKey1);
            assertNotNull(hash1);
            assertEquals(2, hash1.size());
            assertTrue(hash1.containsKey("id"));
            assertEquals(userInfo.userId(), new UserId(hash1.get("id")));
            assertTrue(hash1.containsKey("role"));
            assertEquals(userInfo.role(), hash1.get("role"));
            var hash3 = connection.hgetAll(sessionKey3);
            assertNotNull(hash3);
            assertEquals(2, hash3.size());
            assertTrue(hash3.containsKey("id"));
            assertEquals(userInfo.userId(), new UserId(hash3.get("id")));
            assertTrue(hash3.containsKey("role"));
            assertEquals(userInfo.role(), hash3.get("role"));
        }
    }

    @Test
    void updateUserInfo_positive_notFound() {
        var userInfo = new UserInfo(new UserId(2), "admin_role");
        sessionManager.updateUserInfo(userInfo);
        try (var connection = jedisPool.getResource()) {
            var userKey = String.format("user:%s", userInfo.userId().id());
            var hash = connection.hgetAll(userKey);
            assertNotNull(hash);
            assertTrue(hash.isEmpty());
        }
    }

    @Test
    void endSession_positive_ended() {
        sessionManager.endSession(uuids.getFirst());
        try (var connection = jedisPool.getResource()) {
            var userKey = "user:1";
            var hash = connection.hgetAll(userKey);
            assertNotNull(hash);
            assertEquals(2, hash.size());
            var sessionKey = String.format("session:%s", uuids.getFirst());
            assertFalse(hash.containsKey(sessionKey));
            assertFalse(connection.exists(sessionKey));
        }
    }

    @Test
    void endSession_positive_notFound() {
        sessionManager.endSession(uuids.get(1));
        try (var connection = jedisPool.getResource()) {
            var userKey = "user:1";
            var hash = connection.hgetAll(userKey);
            assertNotNull(hash);
            assertEquals(3, hash.size());
            var sessionKey = String.format("session:%s", uuids.get(1));
            assertTrue(hash.containsKey(sessionKey));
            assertFalse(connection.exists(sessionKey));
        }
    }

    @Test
    void endSessions_positive_ended() {
        sessionManager.endSessions(new UserId(1));
        try (var connection = jedisPool.getResource()) {
            var userKey = "user:1";
            var hash = connection.hgetAll(userKey);
            assertNotNull(hash);
            assertTrue(hash.isEmpty());
            for (var sessionId : uuids) {
                var sessionKey = String.format("session:%s", sessionId);
                assertFalse(connection.exists(sessionKey));
            }
        }
    }

    @Test
    void endSessions_positive_notFound() {
        sessionManager.endSessions(new UserId(2));
        try (var connection = jedisPool.getResource()) {
            var userKey = "user:2";
            var hash = connection.hgetAll(userKey);
            assertNotNull(hash);
            assertTrue(hash.isEmpty());
        }
    }

    @Test
    void generateSessionId_positive_generated() {
        var uuid = sessionManager.generateSessionId();
        try (var connection = jedisPool.getResource()) {
            var sessionKey = String.format("session:%s", uuid);
            assertFalse(connection.exists(sessionKey));
        }
    }

    @AfterEach
    void tearDown() {
        redisContainer.stop();
        redisContainer.close();
        jedisPool.close();
    }
}