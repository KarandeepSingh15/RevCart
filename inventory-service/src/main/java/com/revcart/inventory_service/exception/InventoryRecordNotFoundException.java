package com.revcart.inventory_service.exception;

public class InventoryRecordNotFoundException extends RuntimeException {
    public InventoryRecordNotFoundException(String message) {
        super(message);
    }
}
