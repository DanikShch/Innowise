package com.innowise.userservice.controller;

import com.innowise.userservice.dto.request.UserRequestDto;
import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.dto.response.UserWithCardsResponseDto;
import com.innowise.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserWithCardsResponseDto> getUserById(@PathVariable Long id) {
        UserWithCardsResponseDto userResponseDto = userService.getUserById(id);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        if (email != null) {
            UserResponseDto user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } else {
            Page<UserResponseDto> users = userService.getAllUsers(PageRequest.of(page, size, Sort.by(sort)));
            return ResponseEntity.ok(users.getContent());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.updateUser(id, userRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
