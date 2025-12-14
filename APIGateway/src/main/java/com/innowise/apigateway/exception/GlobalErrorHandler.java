package com.innowise.apigateway.exception;

import com.innowise.apigateway.dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Component
@Order(-1)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @NonNull
    public Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable ex) {
        log.error("Global error handler caught exception: {}", ex.getMessage(), ex);

        HttpStatus status = determineHttpStatus(ex);
        String message = determineErrorMessage(ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(exchange.getRequest().getPath().value())
                .build();

        return writeErrorResponse(exchange, status, errorResponse);
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof NotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private String determineErrorMessage(Throwable ex) {
        if (ex.getMessage() != null) {
            return ex.getMessage();
        } else if (ex instanceof TokenExpiredException) {
            return "Token has expired";
        } else if (ex instanceof TokenValidationException) {
            return "Invalid token";
        } else {
            return "An unexpected error occurred";
        }
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange,
                                          HttpStatus status,
                                          ErrorResponse errorResponse) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize error response", e);
            return writeFallbackError(exchange, status, errorResponse.getMessage());
        }
    }

    private Mono<Void> writeFallbackError(ServerWebExchange exchange,
                                          HttpStatus status,
                                          String message) {
        String fallbackJson = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(fallbackJson.getBytes(StandardCharsets.UTF_8));

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}