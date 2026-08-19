package com._pearls.cms.security;

import com._pearls.cms.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("my_very_secure_and_long_jwt_secret_key_1234567890");
        jwtProperties.setExpirationMs(3600000L); // 1 hour

        jwtService = new JwtService(jwtProperties);
    }

    @Test
    @DisplayName("generateToken and extractClaims should return expected userId and email")
    void testGenerateAndExtractClaims() {
        Long userId = 101L;
        String email = "test@example.com";

        String token = jwtService.generateToken(userId, email);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
        assertEquals(userId, jwtService.extractUserId(token));
        assertEquals(email, jwtService.extractEmail(token));
    }

    @Test
    @DisplayName("isTokenValid should return false for invalid or tampered token")
    void testInvalidToken() {
        assertFalse(jwtService.isTokenValid("invalid.jwt.token"));
        assertFalse(jwtService.isTokenValid(""));
    }

    @Test
    @DisplayName("isTokenValid should return false for expired token")
    void testExpiredToken() {
        JwtProperties expiredProperties = new JwtProperties();
        expiredProperties.setSecret("my_very_secure_and_long_jwt_secret_key_1234567890");
        expiredProperties.setExpirationMs(-1000L); // expired

        JwtService expiredJwtService = new JwtService(expiredProperties);
        String expiredToken = expiredJwtService.generateToken(1L, "expired@example.com");

        assertFalse(jwtService.isTokenValid(expiredToken));
    }
}
