package traintickets.security.session;

import redis.clients.jedis.JedisPool;
import traintickets.businesslogic.model.User;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.session.SessionManager;
import traintickets.businesslogic.transport.UserInfo;

import java.io.Closeable;
import java.util.HashMap;
import java.util.UUID;

public final class SessionManagerImpl implements SessionManager, Closeable {
    private final JedisPool jedisPool;

    public SessionManagerImpl(String redisHost, int redisPort, String redisUsername, String redisPassword) {
        jedisPool = new JedisPool(redisHost, redisPort, redisUsername, redisPassword);
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void startSession(String sessionId, User user) {
        try (var connection = jedisPool.getResource()) {
            var userId = user.id().id().toString();
            var hash = new HashMap<String, String>();
            hash.put("id", userId);
            hash.put("role", user.role());
            var sessionKey = String.format("session:%s", sessionId);
            connection.hset(sessionKey, hash);
            var userKey = String.format("user:%s", userId);
            var sessions = new HashMap<>(connection.hgetAll(userKey));
            sessions.put(sessionKey, "");
            connection.hset(userKey, sessions);
        }
    }

    @Override
    public UserInfo getUserInfo(String sessionId) {
        try (var connection = jedisPool.getResource()) {
            var sessionKey = String.format("session:%s", sessionId);
            var userId = (UserId) null;
            var role = (String) null;
            if (connection.exists(sessionKey)) {
                var data = connection.hgetAll(sessionKey);
                userId = new UserId(data.get("id"));
                role = data.get("role");
            }
            return new UserInfo(userId, role);
        }
    }

    @Override
    public void updateUserInfo(UserInfo userInfo) {
        try (var connection = jedisPool.getResource()) {
            var userId = userInfo.userId().id().toString();
            var hash = new HashMap<String, String>();
            hash.put("id", userId);
            hash.put("role", userInfo.role());
            var userKey = String.format("user:%s", userId);
            if (connection.exists(userKey)) {
                var sessions = connection.hgetAll(userKey);
                var newSessions = new HashMap<String, String>();
                for (var sessionKey : sessions.keySet()) {
                    if (connection.exists(sessionKey)) {
                        connection.hset(sessionKey, hash);
                        newSessions.put(sessionKey, "");
                    }
                }
                connection.del(userKey);
                connection.hset(userKey, newSessions);
            }
        }
    }

    @Override
    public void endSession(String sessionId) {
        try (var connection = jedisPool.getResource()) {
            var sessionKey = String.format("session:%s", sessionId);
            var userKey = String.format("user:%s", connection.hgetAll(sessionKey).get("id"));
            connection.del(sessionKey);
            connection.hdel(userKey, sessionKey);
        }
    }

    @Override
    public void endSessions(UserId userId) {
        try (var connection = jedisPool.getResource()) {
            var userKey = String.format("user:%s", userId.id());
            var sessions = connection.hgetAll(userKey);
            for (var sessionKey : sessions.keySet()) {
                connection.del(sessionKey);
            }
            connection.del(userKey);
        }
    }

    @Override
    public String generateSessionId() {
        try (var connection = jedisPool.getResource()) {
            var uuid = UUID.randomUUID();
            var sessionKey = String.format("session:%s", uuid);
            while (connection.exists(sessionKey)) {
                uuid = UUID.randomUUID();
                sessionKey = String.format("session:%s", uuid);
            }
            return uuid.toString();
        }
    }

    @Override
    public void close() {
        jedisPool.close();
    }
}
