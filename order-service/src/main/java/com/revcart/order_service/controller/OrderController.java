package com.revcart.order_service.controller;

import com.revcart.order_service.dto.CreateOrderRequest;
import com.revcart.order_service.dto.OrderResponse;
import com.revcart.order_service.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestHeader("X-User-Id") Long customerId,
                                     @RequestHeader("X-Role") String role,
                                     @Valid @RequestBody CreateOrderRequest request) {

        return service.createOrder(customerId, role, request);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@RequestHeader("X-User-Id") Long customerId,
                                  @PathVariable Long id) {
        return service.getOrder(id, customerId);
    }

    @GetMapping("/my-orders")
    public List<OrderResponse>
    getMyOrders(@RequestHeader("X-User-Id") Long customerId) {
        return service.getMyOrders(customerId);
    }
}
