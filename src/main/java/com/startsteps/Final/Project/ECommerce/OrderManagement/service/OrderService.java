package com.startsteps.Final.Project.ECommerce.OrderManagement.service;

import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.OrderNotFoundException;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.Order;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.OrderStatus;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.ProductsOrders;
import com.startsteps.Final.Project.ECommerce.OrderManagement.repository.OrderRepository;
import com.startsteps.Final.Project.ECommerce.ProductManagement.models.Product;
import com.startsteps.Final.Project.ECommerce.security.login.models.User;
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

    public List<Order> loadUserOrdersWithStatus(Integer userId, OrderStatus status) {
        return orderRepository.findByUserIdAndOrderStatus(userId, status);
    }

    public Order viewCart(Integer userId) {
        List<Order> orders = loadUserOrdersWithStatus(userId, OrderStatus.IN_CART);
        if (!orders.isEmpty()) {
            return orders.get(0);
        }
        return null;
    }

    public boolean hasCart(Integer userId) {
        return !loadUserOrdersWithStatus(userId, OrderStatus.IN_CART).isEmpty();
    }

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public void createOrder(Order newOrder){
        orderRepository.save(newOrder);
    }

    @Transactional
    public void updateOrder(int id, Order updatedOrder){
        Order order = orderRepository.findById(id).orElse(null);
        order.setOrderDate(updatedOrder.getOrderDate());
        order.setOrderStatus(updatedOrder.getOrderStatus());
    }

    public void deleteOrder(int id){
        orderRepository.deleteById(id);
    }

    @Transactional
    public void addToCart(int userId, Product product, int quantity){
        Order order;
        if (hasCart(userId)){
            order = viewCart(userId);
        } else {
            order = new Order (userId, OrderStatus.IN_CART);
            orderRepository.save(order);
        }
        ProductsOrders productsOrders = new ProductsOrders(order, product, quantity);
        order.addProductOrder(productsOrders);
    }
}
