package com.revcart.payment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.revcart.payment_service", "com.revcart.common_outbox","com.revcart.common_events"})
@EnableJpaRepositories(basePackages = {"com.revcart.payment_service", "com.revcart.common_outbox","com.revcart.common_events"})
@EntityScan(basePackages = {"com.revcart.payment_service", "com.revcart.common_outbox","com.revcart.common_events"})
@EnableScheduling
@EnableJpaAuditing
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
