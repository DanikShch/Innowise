package com.innowise.orderservice.client;

import com.innowise.orderservice.dto.response.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${app.user-service.url}")
public interface UserServiceClient {
    @GetMapping("/api/v1/users")
    UserResponseDto getUserByEmail(@RequestParam String email);

    @GetMapping("/api/v1/users/{id}")
    UserResponseDto getUserById(@PathVariable Long id);
}
