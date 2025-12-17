package com.innowise.authenticationservice.util;

import com.innowise.authenticationservice.config.JwtConfig;
import com.innowise.authenticationservice.model.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String AUTH_KEY = "auth";
    private static final String USER_ID_KEY = "userId";
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }

    public String generateAccessToken(String username, Role role, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration());

        JwtBuilder builder = Jwts.builder()
                .subject(username)
                .claim(AUTH_KEY, role.name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256);

        if (userId != null) {
            builder.claim(USER_ID_KEY, userId);
        }

        return builder.compact();
    }

    public String generateSystemToken(String username, Role role) {
        return generateAccessToken(username, role, null);
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Role getRoleFromToken(String token) {
        String role = parseClaims(token).get(AUTH_KEY, String.class);
        return Role.valueOf(role);
    }

    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get(USER_ID_KEY, Long.class);
    }

    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        Role role = getRoleFromToken(token);

        var authority = new SimpleGrantedAuthority("ROLE_" + role.name());
        return new UsernamePasswordAuthenticationToken(username, null, List.of(authority));
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}