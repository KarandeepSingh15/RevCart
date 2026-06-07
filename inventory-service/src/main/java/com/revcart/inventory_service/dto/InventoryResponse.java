package com.revcart.inventory_service.dto;

public record InventoryResponse(
        Long productId,
        Integer availableQuantity,
        Integer reservedQuantity
) {
}
