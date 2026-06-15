package com.revcart.inventory_service.exception;

public class InsufficientStockAvailabilityException extends RuntimeException {
    public InsufficientStockAvailabilityException(String message) {
        super(message);
    }
}
