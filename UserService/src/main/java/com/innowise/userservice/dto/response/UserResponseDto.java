package com.innowise.userservice.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponseDto {
    private Long id;

    private String name;

    private String surname;

    private LocalDate birthDate;

    private String email;
}
