package com.innowise.userservice.integration;

import com.innowise.userservice.dto.request.UserRequestDto;
import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.dto.response.UserWithCardsResponseDto;
import com.innowise.userservice.exception.EmailAlreadyExistsException;
import com.innowise.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createAndGetUser_Success() {
        UserRequestDto request = new UserRequestDto(
                "Alice", "Smith", LocalDate.of(1995, 5, 10), "alice@example.com"
        );

        UserResponseDto created = userService.createUser(request);

        assertNotNull(created.getId());
        assertEquals("Alice", created.getName());

        UserWithCardsResponseDto found = userService.getUserById(created.getId());
        assertEquals("alice@example.com", found.getEmail());
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        UserRequestDto user1 = new UserRequestDto(
                "Bob", "Marley", LocalDate.of(1980, 3, 12), "bob@example.com"
        );
        userService.createUser(user1);

        UserRequestDto user2 = new UserRequestDto(
                "Robert", "Marley", LocalDate.of(1981, 3, 12), "bob@example.com"
        );

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(user2));
    }
}
