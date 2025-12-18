package com.innowise.authenticationservice.client;

import com.innowise.authenticationservice.dto.response.UserMeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@FeignClient(name = "user-service-client", url = "${gateway.url}")
public interface UserServiceClient {

    @PostMapping("/api/v1/users")
    ResponseEntity<Void> createUser(
            @RequestBody UserCreateRequest request,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/api/v1/users/me")
    UserMeResponse getCurrentUser(@RequestHeader("Authorization") String token);

    record UserCreateRequest(
            String name,
            String surname,
            LocalDate birthDate
    ) {}
}