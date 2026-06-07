package com.revcart.inventory_service.controller;

import com.revcart.inventory_service.dto.InventoryResponse;
import com.revcart.inventory_service.dto.ReserveOrReleaseRequest;
import com.revcart.inventory_service.dto.UpsertInventoryRequest;
import com.revcart.inventory_service.service.IInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_ROLE = "X-Role";
    private final IInventoryService service;

    @PutMapping("/product/{productId}")
    public InventoryResponse upsertInventory(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @PathVariable Long productId,
                                             @Valid @RequestBody UpsertInventoryRequest request) {
        return service.upsertInventory(productId, request);
    }

    @PatchMapping("/product/{productId}/restock")
    public InventoryResponse upsertInventoryRestock(@RequestHeader(HEADER_USER_ID) Long userId,
                                                    @PathVariable Long productId,
                                                    @Valid @RequestBody UpsertInventoryRequest request) {
        return service.upsertRestockInventory(productId, request);
    }

    @GetMapping("/product/{productId}")
    public InventoryResponse getInventory(@RequestHeader(HEADER_USER_ID) Long userId,
                                          @PathVariable Long productId) {

        return service.getInventory(productId);
    }

    @PostMapping("/product/{productId}/reserve")
    public InventoryResponse reserveInventory(@RequestHeader(HEADER_USER_ID) Long userId, @PathVariable Long productId,
                                              @Valid @RequestBody ReserveOrReleaseRequest request) {
        return service.reserveStock(productId,request);
    }

    @PostMapping("/product/{productId}/release")
    public InventoryResponse releaseInventory(@RequestHeader(HEADER_USER_ID) Long userId, @PathVariable Long productId,
                                              @Valid @RequestBody ReserveOrReleaseRequest request) {
        return service.releaseStock(productId,request);
    }

}
