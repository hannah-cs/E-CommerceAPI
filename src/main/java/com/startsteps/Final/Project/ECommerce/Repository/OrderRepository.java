package com.startsteps.Final.Project.ECommerce.Repository;

import com.startsteps.Final.Project.ECommerce.Models.Order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
