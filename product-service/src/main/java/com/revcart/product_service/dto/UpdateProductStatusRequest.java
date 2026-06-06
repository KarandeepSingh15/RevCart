package com.revcart.product_service.dto;

import com.revcart.product_service.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateProductStatusRequest(
        @NotNull
        ProductStatus status
) {
}