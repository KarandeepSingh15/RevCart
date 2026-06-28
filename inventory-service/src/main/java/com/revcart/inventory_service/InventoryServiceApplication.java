package com.revcart.inventory_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.revcart.inventory_service", "com.revcart.common_outbox","com.revcart.common_events"})
@EnableScheduling
@EnableJpaRepositories(basePackages = {"com.revcart.inventory_service", "com.revcart.common_outbox","com.revcart.common_events"})
@EntityScan(basePackages = {"com.revcart.inventory_service", "com.revcart.common_outbox","com.revcart.common_events"})
@EnableJpaAuditing
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}
