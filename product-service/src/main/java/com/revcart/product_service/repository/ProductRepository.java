package com.revcart.product_service.repository;

import com.revcart.product_service.entity.Product;
import com.revcart.product_service.enums.ProductCategories;
import com.revcart.product_service.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findBySellerId(Long sellerId, Pageable pageable);
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    List<Product> findBySellerId(Long sellerId);
    List<Product> findByStatus(ProductStatus status);
    List<Product> findByCategory(ProductCategories categoryId);

}
