package com.revcart.product_service.controller;

import com.revcart.product_service.dto.CreateProductRequest;
import com.revcart.product_service.dto.PageResponse;
import com.revcart.product_service.dto.ProductResponse;
import com.revcart.product_service.dto.UpdateProductRequest;
import com.revcart.product_service.dto.UpdateProductStatusRequest;
import com.revcart.product_service.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public PageResponse<ProductResponse> getProducts(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productService.getAllActiveProducts(pageable);
    }

    @GetMapping("/{productId}")
    public ProductResponse getProduct(@RequestHeader(HEADER_USER_ID) Long userId, @PathVariable Long productId) {
        return productService.getProductById(productId);
    }

    @GetMapping("/my-products")
    public PageResponse<ProductResponse> getMyProducts(
            @RequestHeader(HEADER_USER_ID) Long sellerId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productService.getSellerProducts(sellerId, pageable);
    }
}