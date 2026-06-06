package com.revcart.product_service.dto;

import com.revcart.product_service.enums.ProductCategories;
import com.revcart.product_service.enums.ProductStatus;

import java.math.BigDecimal;

public record ProductResponse(

        Long id,
        Long sellerId,
        String name,
        String description,
        BigDecimal price,
        ProductCategories category,
        ProductStatus status
) {
}