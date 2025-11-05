package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.request.CardInfoRequestDto;
import com.innowise.userservice.dto.response.CardInfoResponseDto;
import com.innowise.userservice.exception.CardNotFoundException;
import com.innowise.userservice.exception.CardNumberAlreadyExistsException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.CardInfoMapper;
import com.innowise.userservice.model.CardInfo;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.CardInfoRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.CardInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CardInfoServiceImpl implements CardInfoService {
    private final CardInfoRepository cardInfoRepository;

    private final UserRepository userRepository;

    private final CardInfoMapper cardInfoMapper;

    @Override
    @Transactional
    @CacheEvict(value = {"cards", "user-cards"}, allEntries = true)
    public CardInfoResponseDto createCard(CardInfoRequestDto cardRequestDto) {
        if (cardInfoRepository.existsByNumber(cardRequestDto.getNumber())) {
            throw new CardNumberAlreadyExistsException(cardRequestDto.getNumber());
        }
        User user = userRepository.findById(cardRequestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(cardRequestDto.getUserId()));
        CardInfo cardInfo = cardInfoMapper.toEntity(cardRequestDto);
        cardInfo.setUser(user);
        return cardInfoMapper.toDto(cardInfoRepository.save(cardInfo));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cards", key = "#id")
    public CardInfoResponseDto getCardById(Long id) {
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        return cardInfoMapper.toDto(cardInfo);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cards", key = "'all'")
    public Page<CardInfoResponseDto> getAllCards(Pageable pageable) {
        Page<CardInfo> cards = cardInfoRepository.findAll(pageable);
        return cards.map(cardInfoMapper::toDto);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"cards", "user-cards"}, allEntries = true)
    public CardInfoResponseDto updateCard(Long id, CardInfoRequestDto cardRequestDto) {
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        if (!cardInfo.getNumber().equals(cardRequestDto.getNumber()) && cardInfoRepository.existsByNumber(cardRequestDto.getNumber())) {
            throw new CardNumberAlreadyExistsException(cardRequestDto.getNumber());
        }
        if (!cardInfo.getUser().getId().equals(cardRequestDto.getUserId()) && !userRepository.existsById(cardRequestDto.getUserId())) {
            throw new UserNotFoundException(cardRequestDto.getUserId());
        }
        cardInfoMapper.updateEntityFromDto(cardRequestDto, cardInfo);
        return cardInfoMapper.toDto(cardInfoRepository.save(cardInfo));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"cards", "user-cards"}, allEntries = true)
    public void deleteCard(Long id) {
        if (!cardInfoRepository.existsById(id)) {
            throw new CardNotFoundException(id);
        }
        cardInfoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user-cards", key = "#userId")
    public List<CardInfoResponseDto> getCardsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        List<CardInfo> cards = cardInfoRepository.findByUserId(userId);
        return cards.stream().map(cardInfoMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardInfoResponseDto> getCurrentUserCards() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return cardInfoRepository.findByUserId(user.getId())
                .stream()
                .map(cardInfoMapper::toDto)
                .collect(Collectors.toList());
    }
}
