package com.startsteps.Final.Project.ECommerce.OrderManagement.repository;

import com.startsteps.Final.Project.ECommerce.OrderManagement.models.ProductsOrders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsOrdersRepository extends JpaRepository<ProductsOrders, Integer> {
}
