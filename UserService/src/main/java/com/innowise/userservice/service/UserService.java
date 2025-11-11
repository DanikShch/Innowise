package com.innowise.userservice.service;

import com.innowise.userservice.dto.request.UserRequestDto;
import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.dto.response.UserWithCardsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserWithCardsResponseDto getUserById(Long id);

    Page<UserResponseDto> getAllUsers(Pageable pageable);

    UserResponseDto getUserByEmail(String email);

    UserResponseDto updateUser(Long id, UserRequestDto userRequestDto);

    void deleteUser(Long id);

    UserWithCardsResponseDto getCurrentUser();

    UserResponseDto updateCurrentUser(UserRequestDto userRequestDto);
}
