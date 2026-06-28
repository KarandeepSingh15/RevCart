package com.revcart.cart_service.service.impl;

import com.revcart.cart_service.dto.ProductResponse;
import com.revcart.cart_service.exception.ProductInactiveException;
import com.revcart.cart_service.exception.ProductServiceUnavailableException;
import com.revcart.cart_service.rest.ProductClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductValidationService {

    private final ProductClient productClient;

    @Retry(name = "product-service")
    @CircuitBreaker(name = "product-service", fallbackMethod = "fallback")
    public ProductResponse getProduct(Long productId) {
        ProductResponse product = productClient.getProduct(productId);

        if (!product.status().equals("ACTIVE")) {
            throw new ProductInactiveException("Product is not active");
        }
        return product;
    }

    private ProductResponse fallback(
            Long productId,
            Exception ex) {
        throw new ProductServiceUnavailableException("Unable to fetch product");
    }
}
