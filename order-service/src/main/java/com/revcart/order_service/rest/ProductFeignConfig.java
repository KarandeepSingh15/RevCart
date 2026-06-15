package com.revcart.order_service.rest;

import feign.Retryer;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class ProductFeignConfig {
    @Bean
    public Retryer feignRetryer() {
        // period: 1000ms (initial interval)
        // maxPeriod: 4000ms (maximum interval cap)
        // maxAttempts: 4 (1 initial call + 3 retries = 4 total attempts)
        return new Retryer.Default(
                1000,
                TimeUnit.SECONDS.toMillis(4),
                4
        );
    }
}
