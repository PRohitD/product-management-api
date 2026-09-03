package com.zestindia.productapi.service;

import com.zestindia.productapi.entity.RefreshToken;
import com.zestindia.productapi.entity.User;
import com.zestindia.productapi.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenDuration;


    // Create a new refresh token
    public RefreshToken createRefreshToken(User user) {

        // Remove old refresh token
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .user(user)
                        .token(UUID.randomUUID().toString())
                        .expiryDate(
                                LocalDateTime.now()
                                        .plusSeconds(
                                                refreshTokenDuration / 1000
                                        )
                        )
                        .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken getByToken(String token) {

        return refreshTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Refresh token not found"
                        )
                );
    }

    // Check whether refresh token is still valid
    public RefreshToken verifyExpiration(
            RefreshToken token
    ) {

        if (token.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            refreshTokenRepository.delete(token);

            throw new RuntimeException(
                    "Refresh token has expired"
            );
        }

        return token;
    }
}