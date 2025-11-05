package com.innowise.userservice.controller;


import com.innowise.userservice.dto.request.CardInfoRequestDto;
import com.innowise.userservice.dto.response.CardInfoResponseDto;
import com.innowise.userservice.service.CardInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardInfoController {
    private final CardInfoService cardInfoService;

    @PostMapping
    public ResponseEntity<CardInfoResponseDto> createCard(@Valid @RequestBody CardInfoRequestDto cardRequestDto) {
        CardInfoResponseDto cardResponse = cardInfoService.createCard(cardRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponseDto> getCardById(@PathVariable Long id) {
        CardInfoResponseDto cardResponse = cardInfoService.getCardById(id);
        return ResponseEntity.ok(cardResponse);
    }

    @GetMapping
    public ResponseEntity<List<CardInfoResponseDto>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        Page<CardInfoResponseDto> cards = cardInfoService.getAllCards(PageRequest.of(page, size, Sort.by(sort)));
        return ResponseEntity.ok(cards.getContent());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CardInfoResponseDto>> getCardsByUserId(@PathVariable Long userId) {
        List<CardInfoResponseDto> cards = cardInfoService.getCardsByUserId(userId);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardInfoResponseDto> updateCard(@PathVariable Long id, @Valid @RequestBody CardInfoRequestDto cardRequestDto) {
        CardInfoResponseDto cardResponse = cardInfoService.updateCard(id, cardRequestDto);
        return ResponseEntity.ok(cardResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardInfoService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-cards")
    public ResponseEntity<List<CardInfoResponseDto>> getMyCards() {
        List<CardInfoResponseDto> cards = cardInfoService.getCurrentUserCards();
        return ResponseEntity.ok(cards);
    }
}
