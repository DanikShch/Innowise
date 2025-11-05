package com.innowise.userservice.client;

import com.innowise.userservice.dto.response.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "${app.auth-service.url:http://localhost:8081}")
public interface AuthServiceClient {

    @PostMapping("/api/v1/auth/validate")
    boolean validateToken(@RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/auth/info")
    UserInfoResponse getUserInfo(@RequestHeader("Authorization") String token);
}