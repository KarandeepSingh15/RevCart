package com.revcart.product_service.dto;

import com.revcart.product_service.enums.ProductCategories;
import com.revcart.product_service.validator.ProductCategoryValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateProductRequest(

        @NotBlank
        String name,
        String description,
        @NotNull
        @Positive
        BigDecimal price,
        @NotBlank
        @ProductCategoryValidator(enumClass = ProductCategories.class)
        String category
) {
}
