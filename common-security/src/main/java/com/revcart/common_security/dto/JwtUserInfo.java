package com.revcart.common_security.dto;

public record JwtUserInfo(
        Long userId,
        String email,
        String role
) {
}
