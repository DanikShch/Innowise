package com.innowise.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class UserWithCardsResponseDto {
    private Long id;

    private String name;

    private String surname;

    private LocalDate birthDate;

    private String email;

    private List<CardInfoResponseDto> cards;
}
