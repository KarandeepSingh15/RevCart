package com.revcart.cart_service.exception;

public class CartOwnershipException extends RuntimeException {
    public CartOwnershipException(String message) {
        super(message);
    }
}
