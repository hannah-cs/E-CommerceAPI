package com.startsteps.Final.Project.ECommerce.OrderManagement.service;

import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.OrderNotFoundException;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.Order;
import com.startsteps.Final.Project.ECommerce.OrderManagement.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Transactional
    public Order loadOrderById(Integer orderId) throws OrderNotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        return optionalOrder.orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
    }

    public List<Order> loadOrdersByUser(Integer userId) throws OrderNotFoundException {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders;
    }

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public void createOrder(Order newOrder){
        orderRepository.save(newOrder);
    }

    public void updateOrder(int id, Order updatedOrder){
        Order order = orderRepository.findById(id).orElse(null);
        order.setOrderDate(updatedOrder.getOrderDate());
        order.setOrderStatus(updatedOrder.getOrderStatus());
    }

    public void deleteOrder(int id){
        orderRepository.deleteById(id);
    }
}
