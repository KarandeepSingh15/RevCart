package com.revcart.order_service.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id,
                              String name,
                              BigDecimal price,
                              String status) {
}
