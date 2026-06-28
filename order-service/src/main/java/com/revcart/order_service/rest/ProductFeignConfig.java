package com.revcart.order_service.rest;

import feign.Retryer;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class ProductFeignConfig {
    @Bean
    public Retryer feignRetryer() {
        // disable feign retry as we would be using resillience4j retry
        return Retryer.NEVER_RETRY;
    }
}
