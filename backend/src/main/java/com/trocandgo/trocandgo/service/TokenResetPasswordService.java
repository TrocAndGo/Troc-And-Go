package com.trocandgo.trocandgo.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenResetPasswordService {

    private final String issuer = "your-app"; // Issuer spécifique pour les tokens de reset
    private final Duration ttl = Duration.ofMinutes(10); // Expiration courte pour les tokens de reset
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public String generateResetToken(String email) {
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(email) // Utiliser l'email pour identifier l'utilisateur
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    public String validateResetToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            Instant expiration = jwt.getExpiresAt();
            if (expiration != null && Instant.now().isAfter(expiration)) {
                throw new RuntimeException("Token has expired");
            }
            return jwt.getSubject(); // Retourner l'email associé au token
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
}
