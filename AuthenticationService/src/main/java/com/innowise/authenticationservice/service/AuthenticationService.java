package com.innowise.authenticationservice.service;

import com.innowise.authenticationservice.client.UserServiceClient;
import com.innowise.authenticationservice.dto.request.LoginRequest;
import com.innowise.authenticationservice.dto.request.RefreshTokenRequest;
import com.innowise.authenticationservice.dto.request.RegisterRequest;
import com.innowise.authenticationservice.dto.response.AuthResponse;
import com.innowise.authenticationservice.dto.response.UserInfoResponse;
import com.innowise.authenticationservice.exception.RegistrationFailedException;
import com.innowise.authenticationservice.model.Role;
import com.innowise.authenticationservice.model.UserCredentials;
import com.innowise.authenticationservice.repository.UserCredentialsRepository;
import com.innowise.authenticationservice.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceClient userServiceClient;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserCredentials user = userCredentialsRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        log.info("User {} successfully logged in", user.getUsername());
        return AuthResponse.of(accessToken, refreshToken);
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userCredentialsRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        UserCredentials user = UserCredentials.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userCredentialsRepository.save(user);
        log.debug("User credentials saved (transactional): {}", request.getUsername());

        try {
            String token = jwtTokenProvider.generateAccessToken(
                    request.getUsername(),
                    Role.USER
            );
            log.debug("Generated JWT token for: {}", request.getUsername());
            userServiceClient.createUser(
                    new UserServiceClient.UserCreateRequest(
                            request.getName(),
                            request.getSurname(),
                            request.getBirthDate()
                    ),
                    "Bearer " + token
            );

            log.info("Registration completed successfully for: {}", request.getUsername());
        } catch (Exception e) {
            log.error("Registration failed for: {}", request.getUsername(), e);
            throw new RegistrationFailedException("Failed to create user profile: " + e.getMessage(), e);
        }
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(request.getRefreshToken());
        UserCredentials user = userCredentialsRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getRole());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        log.info("Token refreshed for user {}", user.getUsername());
        return AuthResponse.of(newAccessToken, newRefreshToken);
    }

    public UserInfoResponse getUserInfo(String token) {
        if (!validateToken(token)) {
            throw new BadCredentialsException("Invalid token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);
        Role role = jwtTokenProvider.getRoleFromToken(token);

        return UserInfoResponse.builder()
                .username(username)
                .role(role)
                .build();
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}