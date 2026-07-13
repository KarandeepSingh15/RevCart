package com.revcart.product_service.service;

import com.revcart.product_service.dto.CreateProductRequest;
import com.revcart.product_service.dto.PageResponse;
import com.revcart.product_service.dto.ProductResponse;
import com.revcart.product_service.dto.UpdateProductRequest;
import com.revcart.product_service.dto.UpdateProductStatusRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {

    ProductResponse createProduct(Long sellerId, String role, CreateProductRequest request);

    ProductResponse updateProduct(Long sellerId, String role, Long productId, UpdateProductRequest request);

    ProductResponse updateProductStatus(Long sellerId, String role, Long productId, UpdateProductStatusRequest request);

    PageResponse<ProductResponse> getAllActiveProducts(Pageable pageable);

    ProductResponse getProductById(Long productId);

    PageResponse<ProductResponse> getSellerProducts(Long sellerId, Pageable pageable);

}
