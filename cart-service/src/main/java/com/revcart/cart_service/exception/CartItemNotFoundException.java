package com.revcart.cart_service.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException() {
        super("Cart item you are trying to update is not found");
    }
    public CartItemNotFoundException(String message) {
        super(message);
    }
}
