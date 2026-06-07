package com.revcart.inventory_service.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpsertInventoryRequest(
        @NotNull @PositiveOrZero Integer quantity
) {
}
