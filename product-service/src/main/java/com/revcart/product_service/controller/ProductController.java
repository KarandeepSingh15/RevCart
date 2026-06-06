package com.revcart.product_service.controller;

import com.revcart.product_service.dto.CreateProductRequest;
import com.revcart.product_service.dto.ProductResponse;
import com.revcart.product_service.dto.UpdateProductRequest;
import com.revcart.product_service.dto.UpdateProductStatusRequest;
import com.revcart.product_service.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_ROLE = "X-Role";
    private final IProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestHeader(HEADER_USER_ID) Long sellerId,
                                         @RequestHeader(HEADER_ROLE) String role,
                                         @Valid @RequestBody CreateProductRequest request) {
        return productService.createProduct(sellerId, role, request);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@RequestHeader(HEADER_USER_ID) Long sellerId,
                                         @RequestHeader(HEADER_ROLE) String role,
                                         @PathVariable Long id,
                                         @Valid @RequestBody UpdateProductRequest request) {
        return productService.updateProduct(sellerId, role, id, request);
    }

    @PatchMapping("/{id}/status")
    public ProductResponse updateStatus(
            @RequestHeader(HEADER_USER_ID) Long sellerId,
            @RequestHeader(HEADER_ROLE) String role,
            @PathVariable Long id,
            @RequestBody UpdateProductStatusRequest request) {
        return productService.updateProductStatus(sellerId, role, id, request);
    }

    @GetMapping
    public List<ProductResponse> getProducts(@RequestHeader(HEADER_USER_ID) Long userId) {
        return productService.getAllActiveProducts();
    }

    @GetMapping("/my-products")
    public List<ProductResponse>
    getMyProducts(@RequestHeader(HEADER_USER_ID) Long sellerId) {
        return productService.getSellerProducts(sellerId);
    }
}