

package com.zestindia.productapi.service;

import com.zestindia.productapi.dto.request.LoginRequest;
import com.zestindia.productapi.dto.request.RegisterRequest;
import com.zestindia.productapi.dto.response.AuthResponse;
import com.zestindia.productapi.entity.Role;
import com.zestindia.productapi.entity.User;
import com.zestindia.productapi.entity.RefreshToken;
import com.zestindia.productapi.repository.UserRepository;
import com.zestindia.productapi.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;


    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(
                request.getUsername()
        )) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
    }


    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user =
                userRepository
                        .findByUsername(
                                request.getUsername()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(
                                user.getRole()
                                        .name()
                                        .replace("ROLE_", "")
                        )
                        .build();


        String accessToken =
                jwtService.generateToken(userDetails);


        RefreshToken refreshToken =
                refreshTokenService
                        .createRefreshToken(user);


        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(
                        refreshToken.getToken()
                )
                .tokenType("Bearer")
                .build();
    }

    public AuthResponse refreshToken(
            String refreshTokenValue)
    {

        RefreshToken refreshToken =
                refreshTokenService.getByToken(
                        refreshTokenValue
                );

        refreshTokenService.verifyExpiration(
                refreshToken
        );

        User user = refreshToken.getUser();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(
                                user.getRole()
                                        .name()
                                        .replace("ROLE_", "")
                        )
                        .build();

        // Generate new access token
        String newAccessToken =
                jwtService.generateToken(userDetails);

        // Rotate refresh token
        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(
                        newRefreshToken.getToken()
                )
                .tokenType("Bearer")
                .build();
    }
}