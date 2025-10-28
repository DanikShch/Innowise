package com.innowise.userservice.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CardInfoResponseDto {
    private Long id;

    private String number;

    private String holder;

    private LocalDate expirationDate;

    private Long userId;
}
