package com.innowise.apigateway.service;

import com.innowise.apigateway.exception.TokenExpiredException;
import com.innowise.apigateway.exception.TokenValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String secret;

    public Claims validateAndParseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(
                            io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                                    secret.getBytes(StandardCharsets.UTF_8)
                            )
                    )
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            throw new TokenExpiredException("Token has expired", e);
        } catch (SignatureException e) {
            log.warn("Invalid token signature: {}", e.getMessage());
            throw new TokenValidationException("Invalid token signature", e);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            throw new TokenValidationException("Invalid token: " + e.getMessage(), e);
        }
    }
}