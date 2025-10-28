package com.innowise.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequestDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @Past(message = "Birth date must be in the past")
    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;
}
