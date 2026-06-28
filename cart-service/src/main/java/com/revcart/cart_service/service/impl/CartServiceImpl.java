package com.revcart.cart_service.service.impl;

import com.revcart.cart_service.dto.*;
import com.revcart.cart_service.entity.Cart;
import com.revcart.cart_service.entity.CartItem;
import com.revcart.cart_service.exception.CartItemNotFoundException;
import com.revcart.cart_service.exception.CartOwnershipException;
import com.revcart.cart_service.repository.CartItemRepository;
import com.revcart.cart_service.repository.CartRepository;
import com.revcart.cart_service.service.ICartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductValidationService productValidationService;

    @Override
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse addItem(Long userId, AddCartItemRequest request) {
        productValidationService.getProduct(request.productId());
        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.productId());
        CartItem item;
        if (existingItem.isPresent()) {
            item = existingItem.get();

            item.setQuantity(item.getQuantity() + request.quantity());

        } else {
            item = CartItem.builder()
                    .productId(request.productId())
                    .quantity(request.quantity())
                    .cart(cart)
                    .build();

            cart.getItems().add(item);

        }
        cartItemRepository.save(item);
        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override

    public CartResponse updateItem(Long userId, Long itemId, UpdateCartItemRequest request) {

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(CartItemNotFoundException::new);

        validateOwnership(item.getCart(), userId);
        item.setQuantity(request.quantity());
        return mapToResponse(item.getCart());
    }

    @Override
    public void removeItem(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(CartItemNotFoundException::new);

        validateOwnership(item.getCart(), userId);
        cartItemRepository.delete(item);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() ->
                        cartRepository.save(
                                Cart.builder()
                                        .userId(userId)
                                        .build()
                        ));
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> items=new ArrayList<>();
        if(cart.getItems()!=null && !cart.getItems().isEmpty()) {
            items = cart.getItems()
                    .stream()
                    .map(this::mapItem)
                    .toList();
        }
        return new CartResponse(cart.getId(), cart.getUserId(), items);
    }

    private CartItemResponse mapItem(CartItem item) {

        ProductResponse product = productValidationService.getProduct(item.getProductId());
        return new CartItemResponse(item.getId(), item.getProductId(), product.name(), product.price(), item.getQuantity());
    }

    private void validateOwnership(Cart cart, Long userId) {
        if (cart == null || cart.getUserId() == null || !cart.getUserId().equals(userId)) {
            throw new CartOwnershipException("User does not own this cart");
        }
    }
}