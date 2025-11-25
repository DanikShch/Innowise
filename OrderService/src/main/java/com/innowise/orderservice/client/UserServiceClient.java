package com.innowise.orderservice.client;

import com.innowise.orderservice.dto.response.UserResponseDto;
import com.innowise.orderservice.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${app.user-service.url}")
public interface UserServiceClient {

    @GetMapping("/api/v1/users/me")
    @CircuitBreaker(name = "userService", fallbackMethod = "getCurrentUserFallback")
    UserResponseDto getCurrentUser();

    @GetMapping("/api/v1/users/{id}")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    UserResponseDto getUserById(@PathVariable Long id);

    default UserResponseDto getCurrentUserFallback(Exception ex) {
        throw new ServiceUnavailableException("User service is unavailable. Please try again later.");
    }

    default UserResponseDto getUserByIdFallback(Long id, Exception ex) {
        throw new ServiceUnavailableException("User service is unavailable. Cannot retrieve user information.");
    }
}