package com.innowise.apigateway.config;

import com.innowise.apigateway.exception.AuthenticationException;
import com.innowise.apigateway.service.JwtTokenService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private final JwtTokenService jwtTokenService;

    private static final String BEARER_PREFIX = "Bearer ";

    public JwtAuthFilter(JwtTokenService jwtTokenService) {
        super(Config.class);
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var request = exchange.getRequest();
            var authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null) {
                throw new AuthenticationException("Missing authorization header");
            }

            if (!authHeader.startsWith(BEARER_PREFIX)) {
                throw new AuthenticationException("Invalid authorization header format");
            }

            String token = authHeader.substring(BEARER_PREFIX.length());

            try {
                Claims claims = jwtTokenService.validateAndParseToken(token);
                log.debug("Token validated successfully for user: {}", claims.getSubject());
                return chain.filter(exchange);

            } catch (Exception e) {
                log.error("Authentication error", e);
                throw new AuthenticationException("Authentication failed: " + e.getMessage());
            }
        };
    }

    public static class Config {
    }
}