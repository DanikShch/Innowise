package com.innowise.authenticationservice.dto.response;

import com.innowise.authenticationservice.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoResponse {
    private String username;
    private Role role;
}