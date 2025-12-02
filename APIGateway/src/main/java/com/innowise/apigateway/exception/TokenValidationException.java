package com.innowise.apigateway.exception;

public class TokenValidationException extends AuthenticationException {
    public TokenValidationException(String message) {
        super(message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}