package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.request.UserRequestDto;
import com.innowise.userservice.dto.response.CardInfoResponseDto;
import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.dto.response.UserWithCardsResponseDto;
import com.innowise.userservice.exception.EmailAlreadyExistsException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.CardInfoService;
import com.innowise.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CardInfoService cardInfoService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException(userRequestDto.getEmail());
        }
        User user = userMapper.toEntity(userRequestDto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserWithCardsResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        List<CardInfoResponseDto> cards = cardInfoService.getCardsByUserId(id);
        UserWithCardsResponseDto dto = userMapper.toDtoWithCards(user);
        dto.setCards(cards);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "'all'")
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#email")
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (!user.getEmail().equals(userRequestDto.getEmail()) && userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException(userRequestDto.getEmail());
        }
        userMapper.updateEntityFromDto(userRequestDto, user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
