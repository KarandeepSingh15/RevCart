package com.revcart.auth_service.service.impl;

import com.revcart.auth_service.dto.AuthResponse;
import com.revcart.auth_service.dto.LoginRequest;
import com.revcart.auth_service.dto.SignupRequest;
import com.revcart.auth_service.entity.User;
import com.revcart.auth_service.enums.Role;
import com.revcart.auth_service.exceptions.InvalidCredentialException;
import com.revcart.auth_service.exceptions.UserAlreadyExistsException;
import com.revcart.auth_service.repository.UserRepository;
import com.revcart.auth_service.service.IAuthService;
import com.revcart.common_security.dto.JwtUserInfo;
import com.revcart.common_security.service.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    @Override
    public AuthResponse registerCustomer(SignupRequest request) {
        validateEmail(request.email());

        User user = getUser(request, Role.CUSTOMER);

        User savedUser =
                userRepository.save(user);

        String token =
                jwtService.generateToken(mapUserToJwtUserInfo(user));

        return new AuthResponse(token);
    }

    private static JwtUserInfo mapUserToJwtUserInfo(User user) {
        return new JwtUserInfo(user.getId(), user.getEmail(), user.getRole().name());
    }

    @Override
    public AuthResponse registerSeller(SignupRequest request) {
        validateEmail(request.email());

        User user = getUser(request, Role.SELLER);

        User savedUser =
                userRepository.save(user);

        String token =
                jwtService.generateToken(mapUserToJwtUserInfo(user));

        return new AuthResponse(token);
    }

    private User getUser(SignupRequest request, Role seller) {
        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(
                        passwordEncoder.encode(
                                request.password()))
                .role(seller)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user =
                userRepository.findByEmail(
                                request.email())
                        .orElseThrow(
                                InvalidCredentialException::new);

        boolean valid =
                passwordEncoder.matches(
                        request.password(),
                        user.getPassword());

        if (!valid) {
            throw new InvalidCredentialException();
        }

        String token =
                jwtService.generateToken(mapUserToJwtUserInfo(user));

        return new AuthResponse(token);
    }

    private void validateEmail(
            String email) {

        if (userRepository.existsByEmail(email)) {

            throw new UserAlreadyExistsException(
                    email);
        }
    }
}
