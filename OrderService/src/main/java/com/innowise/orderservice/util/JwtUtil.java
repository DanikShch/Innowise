package com.innowise.orderservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public String extractEmail(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

        } catch (Exception e) {
            log.error("Error extracting email from JWT: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token");
        }
    }

    public Role extractRole(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String role = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("auth", String.class);

            return Role.valueOf(role);
        } catch (Exception e) {
            log.error("Error extracting role from JWT: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token");
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}