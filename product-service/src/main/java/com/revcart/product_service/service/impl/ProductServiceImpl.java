package com.revcart.product_service.service.impl;

import com.revcart.product_service.dto.CreateProductRequest;
import com.revcart.product_service.dto.ProductResponse;
import com.revcart.product_service.dto.UpdateProductRequest;
import com.revcart.product_service.dto.UpdateProductStatusRequest;
import com.revcart.product_service.entity.Product;
import com.revcart.product_service.enums.ProductCategories;
import com.revcart.product_service.enums.ProductStatus;
import com.revcart.product_service.exception.AccessDeniedException;
import com.revcart.product_service.exception.InvalidStatusException;
import com.revcart.product_service.exception.ProductNotFoundException;
import com.revcart.product_service.repository.ProductRepository;
import com.revcart.product_service.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    public static final String ROLE_SELLER = "SELLER";
    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(Long sellerId, String role, CreateProductRequest request) {
        verifySellerRole(role);
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(ProductCategories.valueOf(request.category()))
                .status(ProductStatus.DRAFT)
                .sellerId(sellerId)
                .build();
        Product saved = productRepository.save(product);
        return mapProductToProductResponse(saved);
    }

    private static void verifySellerRole(String role) {
        if (!ROLE_SELLER.equals(role)) {
            throw new AccessDeniedException("Only sellers can create products");
        }
    }

    @Override
    public ProductResponse updateProduct(Long sellerId, String role, Long productId, UpdateProductRequest request) {
        verifySellerRole(role);
        Product product = getProduct(productId);

        validateOwnership(product, sellerId);

        product.setName(request.name());
        product.setDescription(
                request.description());
        product.setPrice(request.price());
        product.setCategory(ProductCategories.valueOf(request.category()));

        Product saved = productRepository.save(product);

        return mapProductToProductResponse(saved);
    }

    @Override
    public ProductResponse updateProductStatus(Long sellerId, String role, Long productId, UpdateProductStatusRequest request) {
        if (ProductStatus.DRAFT.equals(request.status()))
        {
            throw new InvalidStatusException("Status can be either updated to 'ACTIVE' or 'INACTIVE'");
        }
        verifySellerRole(role);
        Product product = getProduct(productId);
        validateOwnership(product, sellerId);
        product.setStatus(request.status());
        Product saved = productRepository.save(product);

        return mapProductToProductResponse(saved);
    }

    @Override
    public List<ProductResponse> getAllActiveProducts() {
        return productRepository.findByStatus(ProductStatus.ACTIVE).stream().map(ProductServiceImpl::mapProductToProductResponse).toList();
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        return mapProductToProductResponse(getProduct(productId));
    }

    @Override
    public List<ProductResponse> getSellerProducts(Long sellerId) {
        return productRepository.findBySellerId(sellerId).stream().map(ProductServiceImpl::mapProductToProductResponse).toList();
    }

    private static ProductResponse mapProductToProductResponse(Product product) {
        return new ProductResponse(product.getId(), product.getSellerId(), product.getName(), product.getDescription(), product.getPrice(), product.getCategory(), product.getStatus());
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found: " + productId));
    }

    private void validateOwnership(Product product, Long sellerId) {

        if (!product.getSellerId().equals(sellerId)) {
            throw new AccessDeniedException(
                    "You can modify only your own products");
        }
    }

}
