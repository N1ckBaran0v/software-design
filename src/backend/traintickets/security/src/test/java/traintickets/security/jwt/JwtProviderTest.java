package traintickets.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.transport.UserInfo;
import traintickets.security.exception.InvalidTokenException;
import traintickets.security.exception.InvalidUserInfoException;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(new JwtConfig("Tralalelo tralala", 2));
    }

    @Test
    void generateToken_negative_invalidUserInfo() {
        assertThrows(InvalidUserInfoException.class, () -> jwtProvider.generateToken(null));
    }

    @Test
    void generateToken_negative_invalidUserId() {
        var userInfo = new UserInfo(null, "user_role");
        assertThrows(InvalidUserInfoException.class, () -> jwtProvider.generateToken(userInfo));
    }

    @Test
    void generateToken_negative_invalidRole() {
        var userInfo = new UserInfo(new UserId("1"), null);
        assertThrows(InvalidUserInfoException.class, () -> jwtProvider.generateToken(userInfo));
    }

    @Test
    void generateToken_validateToken_positive_generatedAndValidated() {
        var userInfo = new UserInfo(new UserId("1"), "user_role");
        var token = jwtProvider.generateToken(userInfo);
        assertNotNull(token);
        var result = jwtProvider.validateToken(token);
        assertNotNull(result);
        assertEquals(userInfo, result);
    }

    @Test
    void generateToken_validateToken_negative_expired() {
        jwtProvider = new JwtProvider(new JwtConfig("Tralalelo tralala", 0));
        var userInfo = new UserInfo(new UserId("1"), "user_role");
        var token = jwtProvider.generateToken(userInfo);
        assertNotNull(token);
        assertThrows(InvalidTokenException.class, () -> jwtProvider.validateToken(token));
    }

    @Test
    void validateToken_negative_nullToken() {
        assertThrows(InvalidTokenException.class, () -> jwtProvider.validateToken(null));
    }

    @Test
    void validateToken_negative_invalid() {
        assertThrows(InvalidTokenException.class, () -> jwtProvider.validateToken("Bombardiro Crocodilo"));
    }
}