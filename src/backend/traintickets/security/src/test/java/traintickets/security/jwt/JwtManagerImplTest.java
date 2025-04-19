package traintickets.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.session.JwtManager;
import traintickets.businesslogic.transport.UserInfo;
import traintickets.security.exception.InvalidTokenException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtManagerImplTest {
    private JwtManager jwtManager;
    private List<String> tokensList;

    @BeforeEach
    void setUp() {
        var jwtConfig = new JwtConfig("Tralalelo Tralala", 2);
        jwtManager = new JwtManagerImpl(jwtConfig);
        insertData();
    }

    @SuppressWarnings("unchecked")
    Map<String, String> getTokens() {
        try {
            var tokensField = JwtManagerImpl.class.getDeclaredField("tokens");
            tokensField.setAccessible(true);
            return (Map<String, String>) tokensField.get(jwtManager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    Map<String, Set<String>> getUserTokens() {
        try {
            var userTokensField = JwtManagerImpl.class.getDeclaredField("userTokens");
            userTokensField.setAccessible(true);
            return (Map<String, Set<String>>) userTokensField.get(jwtManager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    void insertData() {
        var tokens = getTokens();
        var userTokens = getUserTokens();
        var provider1 = new JwtProvider(new JwtConfig("Tralalelo Tralala", 2));
        var provider2 = new JwtProvider(new JwtConfig("Tralalelo Tralala", 0));
        var userInfo1 = new UserInfo(new UserId("1"), "user_role");
        var userInfo2 = new UserInfo(new UserId("2"), "user_role");
        var userId1 = "1";
        var userId2 = "2";
        var token1 = provider1.generateToken(userInfo1);
        var token2 = provider2.generateToken(userInfo1);
        var token3 = provider1.generateToken(userInfo2);
        tokens.put(token1, userId1);
        tokens.put(token2, userId1);
        tokens.put(token3, userId2);
        userTokens.put(userId1, new HashSet<>());
        userTokens.put(userId2, new HashSet<>());
        userTokens.get(userId1).add(token1);
        userTokens.get(userId1).add(token2);
        userTokens.get(userId2).add(token3);
        tokensList = List.of(token1, token2, token3);
    }

    @Test
    void generateToken_positive_oldUser() {
        var id = "1";
        var userInfo = new UserInfo(new UserId(id), "user_role");
        var token = jwtManager.generateToken(userInfo);
        assertNotNull(token);
        var tokens = getTokens();
        var userTokens = getUserTokens();
        assertTrue(tokens.size() == 3 || tokens.size() == 4);
        assertEquals(2, userTokens.size());
        assertTrue(userTokens.containsKey(id));
        assertTrue(userTokens.get(id).contains(token));
        assertTrue(userTokens.get(id).size() == 2 || userTokens.get(id).size() == 3);
    }

    @Test
    void generateToken_positive_newUser() {
        var id = "3";
        var userInfo = new UserInfo(new UserId(id), "user_role");
        var token = jwtManager.generateToken(userInfo);
        assertNotNull(token);
        var tokens = getTokens();
        var userTokens = getUserTokens();
        assertEquals(4, tokens.size());
        assertEquals(3, userTokens.size());
        assertTrue(userTokens.containsKey(id));
        assertTrue(userTokens.get(id).contains(token));
        assertEquals(1, userTokens.get(id).size());
    }

    @Test
    void validateToken_positive_validated() {
        var userInfo = new UserInfo(new UserId("1"), "user_role");
        var result = jwtManager.validateToken(tokensList.getFirst());
        assertNotNull(result);
        assertEquals(userInfo, result);
    }

    @Test
    void validateToken_negative_expired() {
        var token = tokensList.get(1);
        assertThrows(InvalidTokenException.class, () -> jwtManager.validateToken(token));
        var tokens = getTokens();
        var userTokens = getUserTokens();
        var id = "1";
        assertEquals(2, tokens.size());
        assertEquals(2, userTokens.size());
        assertTrue(userTokens.containsKey(id));
        assertFalse(userTokens.get(id).contains(token));
        assertEquals(1, userTokens.get(id).size());
    }

    @Test
    void validateToken_negative_notFound() {
        var provider = new JwtProvider(new JwtConfig("Tralalelo Tralala", 4));
        var token = provider.generateToken(new UserInfo(new UserId("1"), "user_role"));
        System.out.println(token);
        System.out.println(tokensList.getFirst());
        assertThrows(InvalidTokenException.class, () -> jwtManager.validateToken(token));
        var tokens = getTokens();
        var userTokens = getUserTokens();
        assertEquals(3, tokens.size());
        assertEquals(2, userTokens.size());
    }

    @Test
    void validateToken_negative_invalidToken() {
        assertThrows(InvalidTokenException.class, () -> jwtManager.validateToken("Bombardiro Crocodilo"));
        var tokens = getTokens();
        var userTokens = getUserTokens();
        assertEquals(3, tokens.size());
        assertEquals(2, userTokens.size());
    }

    @Test
    void invalidateToken_positive_last() {
        var token = tokensList.getLast();
        jwtManager.invalidateToken(token);
        var tokens = getTokens();
        var userTokens = getUserTokens();
        var id = "2";
        assertEquals(2, tokens.size());
        assertEquals(1, userTokens.size());
        assertFalse(userTokens.containsKey(id));
    }

    @Test
    void invalidateToken_positive_notLast() {
        var token = tokensList.getFirst();
        jwtManager.invalidateToken(token);
        var tokens = getTokens();
        var userTokens = getUserTokens();
        var id = "1";
        assertEquals(2, tokens.size());
        assertEquals(2, userTokens.size());
        assertTrue(userTokens.containsKey(id));
        assertFalse(userTokens.get(id).contains(token));
        assertEquals(1, userTokens.get(id).size());
    }

    @Test
    void invalidateTokens_positive_last() {
        var userId = new UserId("2");
        jwtManager.invalidateTokens(userId);
        var tokens = getTokens();
        var userTokens = getUserTokens();
        var id = "2";
        assertEquals(2, tokens.size());
        assertEquals(1, userTokens.size());
        assertFalse(userTokens.containsKey(id));
    }

    @Test
    void invalidateTokens_positive_notLast() {
        var userId = new UserId("1");
        jwtManager.invalidateTokens(userId);
        var tokens = getTokens();
        var userTokens = getUserTokens();
        var id = "1";
        assertEquals(1, tokens.size());
        assertEquals(1, userTokens.size());
        assertFalse(userTokens.containsKey(id));
    }
}