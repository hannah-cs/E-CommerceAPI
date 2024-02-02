package com.startsteps.Final.Project.ECommerce.OrderManagement.repository;

import com.startsteps.Final.Project.ECommerce.OrderManagement.models.Order;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByUserId(Integer userId, Pageable pageable);

    Page<Order> findByUserIdAndOrderStatus(Integer userId, OrderStatus status, Pageable pageable);
    List<Order> findByUserIdAndOrderStatus(Integer userId, OrderStatus status);

    List<Order> findByOrderStatus(OrderStatus orderStatus);
    Optional<Order> findFirstByUserIdAndOrderStatus(Integer userId, OrderStatus orderStatus);
}