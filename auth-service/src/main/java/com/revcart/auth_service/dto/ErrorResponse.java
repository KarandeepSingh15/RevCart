package com.revcart.auth_service.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timeStamp,
        String errorMessage,
        String details
) {
}
