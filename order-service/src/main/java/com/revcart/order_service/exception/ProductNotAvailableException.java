package com.revcart.order_service.exception;

public class ProductNotAvailableException extends RuntimeException {
    public ProductNotAvailableException(String message) {
        super("Product Not Available with productId: " + message);
    }
}
