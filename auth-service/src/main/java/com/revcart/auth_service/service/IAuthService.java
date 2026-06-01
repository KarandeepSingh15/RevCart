package com.revcart.auth_service.service;

import com.revcart.auth_service.dto.AuthResponse;
import com.revcart.auth_service.dto.LoginRequest;
import com.revcart.auth_service.dto.SignupRequest;

public interface IAuthService {

    AuthResponse registerCustomer(SignupRequest request);

    AuthResponse registerSeller(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
