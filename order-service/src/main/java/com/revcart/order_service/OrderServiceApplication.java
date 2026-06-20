package com.revcart.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.revcart.order_service", "com.revcart.common_outbox","com.revcart.common_events"})
@EnableFeignClients
@EnableScheduling
@EnableJpaRepositories(basePackages = {"com.revcart.order_service.repository", "com.revcart.common_outbox.repository","com.revcart.common_events"})
@EntityScan(basePackages = {"com.revcart.order_service.entity", "com.revcart.common_outbox.entity","com.revcart.common_events"})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
