package com.innowise.authenticationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDate;

@FeignClient(name = "user-service-client", url = "${gateway.url}")
public interface UserServiceClient {

    @PostMapping("/api/v1/users")
    ResponseEntity<Void> createUser(
            @RequestBody UserCreateRequest request,
            @RequestHeader("Authorization") String token
    );

    record UserCreateRequest(
            String name,
            String surname,
            LocalDate birthDate
    ) {}
}