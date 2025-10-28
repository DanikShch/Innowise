package com.innowise.userservice.unit;

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
import com.innowise.userservice.service.impl.CardInfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardInfoServiceTest {

    @Mock
    private CardInfoRepository cardInfoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardInfoMapper cardInfoMapper;

    @InjectMocks
    private CardInfoServiceImpl cardInfoService;

    private final User userEntity = User.builder().id(1L).name("John").build();
    private final CardInfoRequestDto cardRequestDto = new CardInfoRequestDto(
            "1234567890123456", "JOHN DOE", LocalDate.of(2027, 12, 31), 1L
    );
    private final CardInfo cardEntity = CardInfo.builder()
            .id(1L).number("1234567890123456").holder("JOHN DOE")
            .expirationDate(LocalDate.of(2027, 12, 31)).user(userEntity).build();
    private final CardInfoResponseDto cardResponseDto = new CardInfoResponseDto(
            1L, "1234567890123456", "JOHN DOE", LocalDate.of(2027, 12, 31), 1L
    );

    @Test
    void createCard_Success() {
        when(cardInfoRepository.existsByNumber("1234567890123456")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(cardInfoMapper.toEntity(cardRequestDto)).thenReturn(cardEntity);
        when(cardInfoRepository.save(cardEntity)).thenReturn(cardEntity);
        when(cardInfoMapper.toDto(cardEntity)).thenReturn(cardResponseDto);

        CardInfoResponseDto result = cardInfoService.createCard(cardRequestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cardInfoRepository).save(cardEntity);
    }

    @Test
    void createCard_NumberExists() {
        when(cardInfoRepository.existsByNumber("1234567890123456")).thenReturn(true);

        assertThrows(CardNumberAlreadyExistsException.class, () -> cardInfoService.createCard(cardRequestDto));
    }

    @Test
    void createCard_UserNotFound() {
        when(cardInfoRepository.existsByNumber("1234567890123456")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cardInfoService.createCard(cardRequestDto));
    }

    @Test
    void getCardById_Success() {
        when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardInfoMapper.toDto(cardEntity)).thenReturn(cardResponseDto);

        CardInfoResponseDto result = cardInfoService.getCardById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCardById_NotFound() {
        when(cardInfoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardInfoService.getCardById(1L));
    }

    @Test
    void getAllCards_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardInfo> cardPage = new PageImpl<>(List.of(cardEntity));
        when(cardInfoRepository.findAll(pageable)).thenReturn(cardPage);
        when(cardInfoMapper.toDto(cardEntity)).thenReturn(cardResponseDto);

        Page<CardInfoResponseDto> result = cardInfoService.getAllCards(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getCardsByUserId_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(cardInfoRepository.findByUserId(1L)).thenReturn(List.of(cardEntity));
        when(cardInfoMapper.toDto(cardEntity)).thenReturn(cardResponseDto);

        List<CardInfoResponseDto> result = cardInfoService.getCardsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getCardsByUserId_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> cardInfoService.getCardsByUserId(1L));
    }

    @Test
    void updateCard_Success() {
        CardInfoRequestDto updateDto = new CardInfoRequestDto(
                "9999999999999999", "JOHN SMITH", LocalDate.of(2028, 12, 31), 2L
        );
        User newUser = User.builder().id(2L).name("Jane").build();
        CardInfo updatedCard = CardInfo.builder()
                .id(1L).number("9999999999999999").holder("JOHN SMITH")
                .expirationDate(LocalDate.of(2028, 12, 31)).user(newUser).build();
        CardInfoResponseDto updatedResponse = new CardInfoResponseDto(
                1L, "9999999999999999", "JOHN SMITH", LocalDate.of(2028, 12, 31), 2L
        );

        when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardInfoRepository.existsByNumber("9999999999999999")).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(cardInfoRepository.save(any(CardInfo.class))).thenReturn(updatedCard);
        when(cardInfoMapper.toDto(updatedCard)).thenReturn(updatedResponse);

        CardInfoResponseDto result = cardInfoService.updateCard(1L, updateDto);

        assertNotNull(result);
        assertEquals("JOHN SMITH", result.getHolder());
    }

    @Test
    void updateCard_NotFound() {
        when(cardInfoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardInfoService.updateCard(1L, cardRequestDto));
    }

    @Test
    void updateCard_NumberExists() {
        CardInfoRequestDto updateDto = new CardInfoRequestDto(
                "9999999999999999", "JOHN DOE", LocalDate.of(2027, 12, 31), 1L
        );

        when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardInfoRepository.existsByNumber("9999999999999999")).thenReturn(true);

        assertThrows(CardNumberAlreadyExistsException.class, () -> cardInfoService.updateCard(1L, updateDto));
    }

    @Test
    void updateCard_UserNotFound() {
        CardInfoRequestDto updateDto = new CardInfoRequestDto(
                "9999999999999999", "JOHN DOE", LocalDate.of(2027, 12, 31), 2L
        );

        when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardInfoRepository.existsByNumber("9999999999999999")).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> cardInfoService.updateCard(1L, updateDto));
    }

    @Test
    void deleteCard_Success() {
        when(cardInfoRepository.existsById(1L)).thenReturn(true);

        cardInfoService.deleteCard(1L);

        verify(cardInfoRepository).deleteById(1L);
    }

    @Test
    void deleteCard_NotFound() {
        when(cardInfoRepository.existsById(1L)).thenReturn(false);

        assertThrows(CardNotFoundException.class, () -> cardInfoService.deleteCard(1L));
    }
}