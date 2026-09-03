package com.zestindia.productapi.repository;

import com.zestindia.productapi.entity.RefreshToken;
import com.zestindia.productapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}