package com.revcart.order_service.service.impl;

import com.revcart.order_service.dto.ProductResponse;
import com.revcart.order_service.exception.ProductServiceUnavailableException;
import com.revcart.order_service.rest.ProductClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFetchService {
    private final ProductClient productClient;


    @Retry(name = "product-service")
    @CircuitBreaker(name = "product-service", fallbackMethod = "fallback")
    public ProductResponse getProduct(Long productId) {
        return productClient.getProduct(productId);
    }

    private ProductResponse fallback(Long productId, Exception ex) {
        throw new ProductServiceUnavailableException("Unable to fetch product details,please try again later");
    }
}
