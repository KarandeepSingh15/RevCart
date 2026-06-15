package com.revcart.order_service.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super("Order not found with orderId: " + message);
    }
}
