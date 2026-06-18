package com.revcart.common_events.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final ObjectMapper objectMapper;

    public <T> T read(String payload, Class<T> clazz) {
        try {
            return objectMapper.readValue(payload, clazz);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
