package com.revcart.product_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class TestController {

    @GetMapping("/test")
    public Map<String, Object> test(@RequestHeader("X-User-Id") String userId,
                                    @RequestHeader("X-Role") String role,
                                    @RequestHeader("X-User-Email") String email) {
        return Map.of(
                "userId", userId,
                "role", role,
                "email", email
        );
    }

    @GetMapping("/hi")
    public String hi() {
        return "hi";
    }
}