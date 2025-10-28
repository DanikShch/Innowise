package com.innowise.userservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CardInfoRequestDto {
    @Pattern(regexp = "^[0-9]{13,19}$", message = "Card number must be 13-19 digits")
    @NotBlank(message = "Number is required")
    private String number;

    @NotBlank(message = "Holder is required")
    private String holder;

    @Future(message = "Expiration date must be in the future")
    @NotNull(message = "Expiration date is required")
    private LocalDate expirationDate;

    @NotNull(message = "User ID is required")
    private Long userId;
}
