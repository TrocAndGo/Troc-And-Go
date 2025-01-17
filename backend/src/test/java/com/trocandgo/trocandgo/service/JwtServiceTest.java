package com.trocandgo.trocandgo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String ISSUER = "TestApp";
    private static final java.time.Duration JWT_TTL = java.time.Duration.ofMinutes(60);
    private static final String USERNAME = "testUser";
    private static final List<String> ROLES = List.of("ADMIN", "USER");
    private static final String TOKEN_VALUE = "mockedJwtToken";

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private Jwt jwt;

    @Test
    void generateToken_validUsername_returnsToken() {
        final var jwtService = new JwtService(ISSUER, JWT_TTL, jwtEncoder, jwtDecoder, redisTemplate);

        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn(TOKEN_VALUE);

        final var token = jwtService.generateToken(USERNAME, ROLES);

        assertThat(token).isEqualTo(TOKEN_VALUE);
    }

    @Test
    void extractUserInfoFromToken_validToken_returnsUserInfo() {
        final var jwtService = new JwtService(ISSUER, JWT_TTL, jwtEncoder, jwtDecoder, redisTemplate);

        when(jwtDecoder.decode(TOKEN_VALUE)).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn(USERNAME);
        when(jwt.getClaim("roles")).thenReturn(ROLES);
        when(jwt.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600)); // Expiration dans le futur

        JwtService.UserInfo userInfo = jwtService.extractUserInfoFromToken(TOKEN_VALUE);

        assertThat(userInfo.getUsername()).isEqualTo(USERNAME);
        assertThat(userInfo.getRoles()).containsExactlyElementsOf(ROLES);
    }

    @Test
    void extractUserInfoFromToken_revokedToken_throwsException() {
        final var jwtService = new JwtService(ISSUER, JWT_TTL, jwtEncoder, jwtDecoder, redisTemplate);

        when(redisTemplate.hasKey(TOKEN_VALUE)).thenReturn(true); // Simuler un token révoqué

        assertThatThrownBy(() -> jwtService.extractUserInfoFromToken(TOKEN_VALUE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token is revoked");
    }

    @Test
    void extractUserInfoFromToken_expiredToken_throwsException() {
        final var jwtService = new JwtService(ISSUER, JWT_TTL, jwtEncoder, jwtDecoder, redisTemplate);

        when(jwtDecoder.decode(TOKEN_VALUE)).thenReturn(jwt);
        when(jwt.getExpiresAt()).thenReturn(Instant.now().minusSeconds(3600)); // Expiration dans le passé

        assertThatThrownBy(() -> jwtService.extractUserInfoFromToken(TOKEN_VALUE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token has expired");
    }

    @Test
    void extractUserInfoFromToken_invalidToken_throwsException() {
        final var jwtService = new JwtService(ISSUER, JWT_TTL, jwtEncoder, jwtDecoder, redisTemplate);

        when(jwtDecoder.decode(TOKEN_VALUE)).thenThrow(new RuntimeException("Invalid JWT token"));

        assertThatThrownBy(() -> jwtService.extractUserInfoFromToken(TOKEN_VALUE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid JWT token");
    }
}
