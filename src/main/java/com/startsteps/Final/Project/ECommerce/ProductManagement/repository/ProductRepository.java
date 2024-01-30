package com.startsteps.Final.Project.ECommerce.ProductManagement.repository;

import com.startsteps.Final.Project.ECommerce.ProductManagement.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findById(int productId);
    Optional<Product> findByProductName(String productName);
    Boolean existsByProductId(int productId);
    Boolean existsByProductName(String productName);
}
