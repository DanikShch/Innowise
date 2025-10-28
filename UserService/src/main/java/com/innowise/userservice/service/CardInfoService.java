package com.innowise.userservice.service;

import com.innowise.userservice.dto.request.CardInfoRequestDto;
import com.innowise.userservice.dto.response.CardInfoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardInfoService {
    CardInfoResponseDto createCard(CardInfoRequestDto cardRequestDto);

    CardInfoResponseDto getCardById(Long id);

    Page<CardInfoResponseDto> getAllCards(Pageable pageable);

    CardInfoResponseDto updateCard(Long id, CardInfoRequestDto cardRequestDto);

    void deleteCard(Long id);

    List<CardInfoResponseDto> getCardsByUserId(Long userId);
}
