package com.zestindia.productapi.service;

import com.zestindia.productapi.dto.request.LoginRequest;
import com.zestindia.productapi.dto.request.RegisterRequest;
import com.zestindia.productapi.dto.response.AuthResponse;
import com.zestindia.productapi.entity.RefreshToken;
import com.zestindia.productapi.entity.Role;
import com.zestindia.productapi.entity.User;
import com.zestindia.productapi.repository.UserRepository;
import com.zestindia.productapi.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthService authService;

    @BeforeEach
    void setUp() {

        authService = new AuthService(
                userRepository,
                authenticationManager,
                passwordEncoder,
                jwtService,
                refreshTokenService
        );
    }

    @Test
    void shouldRegisterUser() {

        RegisterRequest request =
                new RegisterRequest();

        request.setUsername("rohit");
        request.setPassword("rohit123");

        when(userRepository.existsByUsername("rohit"))
                .thenReturn(false);

        when(passwordEncoder.encode("rohit123"))
                .thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(1L)
                .username("rohit")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        authService.register(request);

        verify(userRepository)
                .existsByUsername("rohit");

        verify(passwordEncoder)
                .encode("rohit123");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateUsername() {

        RegisterRequest request =
                new RegisterRequest();

        request.setUsername("rohit");
        request.setPassword("rohit123");

        when(userRepository.existsByUsername("rohit"))
                .thenReturn(true);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "Username already exists",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByUsername("rohit");

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request =
                new LoginRequest();

        request.setUsername("rohit");
        request.setPassword("rohit123");

        User user = User.builder()
                .id(1L)
                .username("rohit")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        when(userRepository.findByUsername("rohit"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(any(UserDetails.class)))
                .thenReturn("access-token");

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .id(1L)
                        .token("refresh-token")
                        .user(user)
                        .expiryDate(
                                LocalDateTime.now().plusDays(7)
                        )
                        .build();

        when(refreshTokenService.createRefreshToken(user))
                .thenReturn(refreshToken);

        AuthResponse response =
                authService.login(request);

        assertNotNull(response);

        assertEquals(
                "access-token",
                response.getAccessToken()
        );

        assertEquals(
                "refresh-token",
                response.getRefreshToken()
        );

        assertEquals(
                "Bearer",
                response.getTokenType()
        );

        verify(authenticationManager)
                .authenticate(any());

        verify(jwtService)
                .generateToken(any(UserDetails.class));

        verify(refreshTokenService)
                .createRefreshToken(user);
    }

    @Test
    void shouldRefreshToken() {

        User user = User.builder()
                .id(1L)
                .username("rohit")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();

        RefreshToken oldRefreshToken =
                RefreshToken.builder()
                        .id(1L)
                        .token("old-refresh-token")
                        .user(user)
                        .expiryDate(
                                LocalDateTime.now().plusDays(7)
                        )
                        .build();

        when(refreshTokenService.getByToken(
                "old-refresh-token"
        )).thenReturn(oldRefreshToken);

        when(refreshTokenService.verifyExpiration(
                oldRefreshToken
        )).thenReturn(oldRefreshToken);

        when(jwtService.generateToken(
                any(UserDetails.class)
        )).thenReturn("new-access-token");

        RefreshToken newRefreshToken =
                RefreshToken.builder()
                        .id(2L)
                        .token("new-refresh-token")
                        .user(user)
                        .expiryDate(
                                LocalDateTime.now().plusDays(7)
                        )
                        .build();

        when(refreshTokenService.createRefreshToken(user))
                .thenReturn(newRefreshToken);

        AuthResponse response =
                authService.refreshToken(
                        "old-refresh-token"
                );

        assertNotNull(response);

        assertEquals(
                "new-access-token",
                response.getAccessToken()
        );

        assertEquals(
                "new-refresh-token",
                response.getRefreshToken()
        );

        assertEquals(
                "Bearer",
                response.getTokenType()
        );

        verify(refreshTokenService)
                .getByToken("old-refresh-token");

        verify(refreshTokenService)
                .verifyExpiration(oldRefreshToken);

        verify(refreshTokenService)
                .createRefreshToken(user);

        verify(jwtService)
                .generateToken(any(UserDetails.class));
    }
}