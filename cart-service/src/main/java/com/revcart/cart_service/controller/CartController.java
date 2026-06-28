package com.revcart.cart_service.controller;

import com.revcart.cart_service.dto.AddCartItemRequest;
import com.revcart.cart_service.dto.CartResponse;
import com.revcart.cart_service.dto.UpdateCartItemRequest;
import com.revcart.cart_service.service.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;

    @GetMapping
    public CartResponse getCart(@RequestHeader("X-User-Id") Long userId) {
        return cartService.getCart(userId);
    }

    @PostMapping("/items")
    public CartResponse addItem(@RequestHeader("X-User-Id") Long userId, @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(userId, request);
    }

    @PatchMapping("/items/{itemId}")
    public CartResponse updateItem(@RequestHeader("X-User-Id") Long userId, @PathVariable Long itemId, @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(userId, itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@RequestHeader("X-User-Id") Long userId, @PathVariable Long itemId) {
        cartService.removeItem(userId, itemId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
    }

}
