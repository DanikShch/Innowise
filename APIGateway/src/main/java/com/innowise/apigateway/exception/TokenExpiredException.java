package com.innowise.apigateway.exception;

public class TokenExpiredException extends TokenValidationException {
    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
