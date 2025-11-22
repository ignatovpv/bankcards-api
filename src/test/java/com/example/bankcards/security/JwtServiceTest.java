package com.example.bankcards.security;

import com.example.bankcards.config.JwtProperties;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtServiceTest {
    private JwtService createTestJwtService() {
        String secret = Base64.getEncoder()
                .encodeToString("0123456789_0123456789_0123456789__".getBytes());

        JwtProperties properties = new JwtProperties(
                secret,
                Duration.ofMinutes(5)
        );
        return new JwtService(properties);
    }

    @Test
    void generateToken_parsedSuccessfully_whenTokenIsValid() {
        JwtService jwtService = createTestJwtService();
        String username = "user";
        String role = "USER";

        String token = jwtService.generateToken(username, role);

        assertDoesNotThrow(() -> jwtService.validateToken(token));
        assertEquals("user", jwtService.extractUsername(token));
        assertEquals("USER", jwtService.extractRole(token));
    }
}
