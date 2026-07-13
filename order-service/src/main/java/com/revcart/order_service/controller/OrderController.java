package com.revcart.order_service.controller;

import com.revcart.order_service.dto.CreateOrderRequest;
import com.revcart.order_service.dto.OrderResponse;
import com.revcart.order_service.dto.PageResponse;
import com.revcart.order_service.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PageResponse<OrderResponse> getMyOrders(
            @RequestHeader("X-User-Id") Long customerId,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "id")   String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return service.getMyOrders(customerId, pageable);
    }
}
