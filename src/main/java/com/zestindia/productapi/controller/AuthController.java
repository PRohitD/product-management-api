package com.zestindia.productapi.controller;

import com.zestindia.productapi.dto.request.LoginRequest;
import com.zestindia.productapi.dto.request.RegisterRequest;
import com.zestindia.productapi.dto.response.AuthResponse;
import com.zestindia.productapi.service.AuthService;
import com.zestindia.productapi.dto.request.RefreshTokenRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<?> register(

            @RequestBody
            RegisterRequest request
    ) {

        authService.register(request);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "User registered successfully"
                )
        );
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(

            @RequestBody
            LoginRequest request
    ) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {

        return ResponseEntity.ok(
                authService.refreshToken(
                        request.getRefreshToken()
                )
        );
    }
}