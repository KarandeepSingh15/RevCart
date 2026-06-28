package com.revcart.cart_service.rest;

import feign.Retryer;
import org.springframework.context.annotation.Bean;

public class ProductClientConfig {
    @Bean
    public Retryer feignRetryer() {
        // disable feign retry as we would be using resillience4j retry
        return Retryer.NEVER_RETRY;
    }
}
