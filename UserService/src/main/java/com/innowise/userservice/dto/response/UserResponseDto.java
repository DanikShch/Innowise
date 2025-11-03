package com.innowise.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserResponseDto {
    private Long id;

    private String name;

    private String surname;

    private LocalDate birthDate;

    private String email;
}
