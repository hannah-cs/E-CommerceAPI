package com.startsteps.Final.Project.ECommerce.OrderManagement.repository;

import com.startsteps.Final.Project.ECommerce.OrderManagement.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(int userId);
}
