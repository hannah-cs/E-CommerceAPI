package com.startsteps.Final.Project.ECommerce.OrderManagement.repository;

import com.startsteps.Final.Project.ECommerce.OrderManagement.models.Order;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(int userId);
    List<Order> findByUserIdAndOrderStatus(Integer userId, OrderStatus orderStatus);

    List<Order> findByOrderStatus(OrderStatus orderStatus);
}