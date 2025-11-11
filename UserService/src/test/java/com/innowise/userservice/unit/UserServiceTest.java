package com.innowise.userservice.unit;

import com.innowise.userservice.dto.request.UserRequestDto;
import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.dto.response.UserWithCardsResponseDto;
import com.innowise.userservice.exception.EmailAlreadyExistsException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.CardInfoService;
import com.innowise.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private CardInfoService cardInfoService;

    private final UserRequestDto userRequestDto = new UserRequestDto(
            "John", "Doe", LocalDate.of(1990, 1, 1)
    );

    private final User userEntity = User.builder()
            .id(1L).name("John").surname("Doe")
            .birthDate(LocalDate.of(1990, 1, 1)).email("john@example.com")
            .build();

    private final UserResponseDto userResponseDto = new UserResponseDto(
            1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com"
    );

    @Test
    void createUser_Success() {
        try (var mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(SecurityContextHolder.getContext()).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("john@example.com");

            when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(userMapper.toEntity(userRequestDto)).thenReturn(userEntity);
            when(userRepository.save(userEntity)).thenReturn(userEntity);
            when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

            UserResponseDto result = userService.createUser(userRequestDto);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(userRepository).save(userEntity);
        }
    }

    @Test
    void createUser_EmailExists() {
        try (var mockedSecurity = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(SecurityContextHolder.getContext()).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("john@example.com");

            when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

            assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(userRequestDto));
        }
    }

    @Test
    void getUserById_Success() {
        UserWithCardsResponseDto userWithCardsDto = new UserWithCardsResponseDto(
                1L, "John", "Doe", LocalDate.now(), "john@test.com", List.of()
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDtoWithCards(userEntity)).thenReturn(userWithCardsDto);
        when(cardInfoService.getCardsByUserId(1L)).thenReturn(List.of());
        UserWithCardsResponseDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getAllUsers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(userEntity));
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

        Page<UserResponseDto> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getUserByEmail_Success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getUserByEmail("john@example.com");

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void getUserByEmail_NotFound() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("john@example.com"));
    }

    @Test
    void updateUser_Success() {
        UserRequestDto updateDto = new UserRequestDto("John", "Smith", LocalDate.of(1990, 1, 1));
        User updatedUser = User.builder().id(1L).name("John").surname("Smith")
                .birthDate(LocalDate.of(1990, 1, 1)).email("john@example.com").build();
        UserResponseDto updatedResponse = new UserResponseDto(1L, "John", "Smith",
                LocalDate.of(1990, 1, 1), "john@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(updatedResponse);

        UserResponseDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals("Smith", result.getSurname());
    }

    @Test
    void updateUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, userRequestDto));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }
}