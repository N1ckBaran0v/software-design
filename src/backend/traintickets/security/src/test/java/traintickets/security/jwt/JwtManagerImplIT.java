package traintickets.security.jwt;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traintickets.businesslogic.jwt.JwtManager;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.transport.UserInfo;
import traintickets.security.exception.InvalidTokenException;

import static org.junit.jupiter.api.Assertions.*;

class JwtManagerImplIT {
    private RedisContainer redisContainer;
    private JwtManager jwtManager;

    @BeforeEach
    void setUp() {
        redisContainer = new RedisContainer("redis");
        redisContainer.start();
        var jwtConfig = new JwtConfig("test", 1);
        System.out.println(redisContainer.getRedisHost() + ":" + redisContainer.getRedisPort());
        var jedisConfig = new JedisConfig(redisContainer.getRedisHost(), redisContainer.getRedisPort(), null);
        jwtManager = new JwtManagerImpl(jwtConfig, jedisConfig);
    }

    @Test
    void all_positive_integration() {
        var id = new UserId("1");
        var role = "user_role";
        var userInfo = new UserInfo(id, role);
        var token = jwtManager.generateToken(id, role);
        assertEquals(userInfo, jwtManager.validateToken(token));
        jwtManager.updateUser(id);
        assertThrows(InvalidTokenException.class, () -> jwtManager.validateToken(token));
        var newToken = jwtManager.generateToken(id, role);
        assertNotNull(jwtManager.validateToken(newToken).version());
        jwtManager.updateUser(id);
        assertThrows(InvalidTokenException.class, () -> jwtManager.validateToken(newToken));
        var newestToken = jwtManager.generateToken(id, role);
        assertNotNull(jwtManager.validateToken(newestToken).version());
    }

    @AfterEach
    void tearDown() {
        redisContainer.stop();
        redisContainer.close();
    }
}