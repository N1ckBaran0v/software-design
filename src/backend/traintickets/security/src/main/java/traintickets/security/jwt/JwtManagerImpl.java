package traintickets.security.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.jwt.JwtManager;
import traintickets.businesslogic.transport.UserInfo;
import traintickets.security.exception.InvalidTokenException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class JwtManagerImpl implements JwtManager {
    private final JwtProvider jwtProvider;
    private final Map<String, String> tokens = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userTokens = new HashMap<>();

    @SuppressWarnings("all")
    public JwtManagerImpl(JwtConfig jwtConfig) {
        jwtProvider = new JwtProvider(jwtConfig);
        Thread.ofVirtual().start(() -> {
            var time = 1000L * 60 * 60 * jwtConfig.expiration();
            while (true) {
                try {
                    Thread.sleep(time);
                    synchronized (tokens) {
                        var users = new ArrayList<>(userTokens.keySet());
                        for (var user : users) {
                            var tokenSet = userTokens.get(user);
                            var tokenList = new ArrayList<>(tokenSet);
                            for (var token : tokenList) {
                                try {
                                    jwtProvider.validateToken(token);
                                } catch (InvalidTokenException e) {
                                    tokenSet.remove(token);
                                }
                            }
                            if (tokenSet.isEmpty()) {
                                userTokens.remove(user);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    @Override
    public String generateToken(UserInfo userInfo) {
        synchronized (tokens) {
            var token = jwtProvider.generateToken(userInfo);
            var userId = userInfo.userId().id();
            tokens.put(token, userId);
            if (!userTokens.containsKey(userId)) {
                userTokens.put(userId, new HashSet<>());
            }
            userTokens.get(userId).add(token);
            return token;
        }
    }

    @Override
    public UserInfo validateToken(String token) {
        try {
            if (!tokens.containsKey(token)) {
                throw new InvalidTokenException(token);
            }
            return jwtProvider.validateToken(token);
        } catch (InvalidTokenException e) {
            if (e.getCause() instanceof TokenExpiredException) {
                invalidateToken(token);
            }
            throw e;
        }
    }

    @Override
    public void invalidateToken(String token) {
        synchronized (tokens) {
            var userId = tokens.get(token);
            if (userId != null) {
                tokens.remove(token);
                var curr = userTokens.get(userId);
                curr.remove(token);
                if (curr.isEmpty()) {
                    userTokens.remove(userId);
                }
            }
        }
    }

    @Override
    public void invalidateTokens(UserId userId) {
        synchronized (tokens) {
            var curr = userTokens.get(userId.id());
            if (curr != null) {
                curr.forEach(tokens::remove);
                userTokens.remove(userId.id());
            }
        }
    }
}
