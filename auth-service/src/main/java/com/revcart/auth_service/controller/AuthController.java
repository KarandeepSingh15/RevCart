package com.revcart.auth_service.controller;

import com.revcart.auth_service.dto.AuthResponse;
import com.revcart.auth_service.dto.LoginRequest;
import com.revcart.auth_service.dto.SignupRequest;
import com.revcart.auth_service.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register/customer")
    public AuthResponse registerCustomer(
            @Valid
            @RequestBody SignupRequest request) {

        return authService
                .registerCustomer(request);
    }

    @PostMapping("/register/seller")
    public AuthResponse registerSeller(
            @Valid
            @RequestBody SignupRequest request) {

        return authService
                .registerSeller(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid
            @RequestBody LoginRequest request) {

        return authService.login(request);
    }
}
