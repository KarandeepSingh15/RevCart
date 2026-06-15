package com.revcart.inventory_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ReserveOrReleaseRequest(
        @NotNull @PositiveOrZero Integer quantity
) {
}
