package com.trocandgo.trocandgo.services;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final String issuer;
    private final Duration ttl;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final RedisTemplate<String, String> redisTemplate;

    public String generateToken(final String username, final List<String> roles) {

        logger.info("Appel de la fonction generateToken!");

        final var issuedAt = Instant.now();

        final var claimsSet = JwtClaimsSet.builder()
                .subject(username)
                .issuer(issuer)
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plus(ttl))
                .claim("roles", roles)
                .build();

        try {
            return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    public void revokeToken(String token) {
        try {
            logger.info("Appel de la fonction revokeToken!");

            // Ajoute le token à la liste des révoqués
            redisTemplate.opsForValue().set(token, "revoked", 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("Problème lors de l'ajout du token à Redis", e);
            throw new RuntimeException("Failed to revoke token", e);
        }
    }

    public boolean isTokenRevoked(String token) {
        logger.info("Appel de la fonction isTokenRevoked!");
        // Si le token existe dans la liste des révoqués, il est révoqué
        return redisTemplate.hasKey(token);
    }

    public UserInfo extractUserInfoFromToken(String token) {
        logger.info("Appel de la fonction extractUserInfoFromToken!");
        try {
            // Vérifier si le token est révoqué
            if (isTokenRevoked(token)) {
                logger.warn("Token is revoked");
                throw new RuntimeException("Token is revoked");
            }

            // Décoder le JWT
            Jwt decodedJwt = jwtDecoder.decode(token);

            // Vérifier l'expiration
            Instant expiration = decodedJwt.getExpiresAt();
            if (expiration != null && Instant.now().isAfter(expiration)) {
                logger.warn("Token has expired");
                throw new RuntimeException("Token has expired");
            }

            // Extraire le username et les rôles
            String username = decodedJwt.getSubject();
            List<String> roles = decodedJwt.getClaim("roles");

            return new UserInfo(username, roles);

        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token");
        } catch (Exception e) {
            logger.error("Error processing token: {}", e.getMessage());
            throw new RuntimeException("Error processing token");
        }
    }

    // Classe interne pour encapsuler les informations de l'utilisateur
    public static class UserInfo {
        private final String username;
        private final List<String> roles;

        public UserInfo(String username, List<String> roles) {
            this.username = username;
            this.roles = roles;
        }

        public String getUsername() {
            return username;
        }

        public List<String> getRoles() {
            return roles;
        }
    }

}
