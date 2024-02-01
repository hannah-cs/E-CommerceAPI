package com.startsteps.Final.Project.ECommerce.OrderManagement.service;

import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.OrderNotFoundException;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.Order;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.OrderStatus;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.ProductsOrders;
import com.startsteps.Final.Project.ECommerce.OrderManagement.repository.OrderRepository;
import com.startsteps.Final.Project.ECommerce.ProductManagement.models.Product;
import com.startsteps.Final.Project.ECommerce.security.login.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public void checkoutOrder(int orderId){
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null){
            System.out.println("No order found with id "+orderId);
        } else {
            if (order.getOrderStatus() != OrderStatus.IN_CART){
                System.out.println("Order already processed");
            } else {
                order.setOrderStatus(OrderStatus.PROCESSING);
                order.setOrderDate(LocalDateTime.now());
                System.out.println("Order placed successfully.");
            }
        }
    }

    public void cancelOrder(int orderId){
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null){
            System.out.println("No order found with id "+orderId);
        } else {
            if (order.getOrderStatus() == OrderStatus.CANCELLED){
                System.out.println("Order already cancelled.");
            } else if (order.getOrderStatus() == OrderStatus.SHIPPED){
                System.out.println("Order has already been shipped. Cannot be cancelled.");
            } else {
                order.setOrderStatus(OrderStatus.CANCELLED);
                order.setOrderDate(LocalDateTime.now());
                System.out.println("Order cancelled successfully.");
            }
        }
    }

    public void returnOrder(int orderId){
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null){
            System.out.println("No order found with id "+orderId);
        } else {
            if (order.getOrderStatus() == OrderStatus.SHIPPED){
                order.setOrderStatus(OrderStatus.RETURNED);
                System.out.println("Return process started successfully.");
            } else if (order.getOrderStatus() == OrderStatus.COMPLETED){
                System.out.println("Error: return window exceeded");
            } else {
                System.out.println("Error: return could not be processed. Double check order status.");
            }
        }
    }

    // for admins
    public void shipOrder(int orderId){
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null){
            System.out.println("No order found with id "+orderId);
        } else {
            if (order.getOrderStatus() == OrderStatus.PROCESSING){
                order.setOrderStatus(OrderStatus.SHIPPED);
                order.setShipDate(LocalDateTime.now());
                System.out.println("Order shipped successfully.");
            } else {
                System.out.println("Error: order cancelled or already shipped");
            }
        }
    }

    // auto mark orders as completed if not returned within 30 days of shipping
    @Scheduled(cron = "0 0 0 * * ?")
    public void completeShippedOrders() {
        List<Order> deliveredOrders = orderRepository.findByOrderStatus(OrderStatus.SHIPPED);
        for (Order order : deliveredOrders) {
            LocalDateTime shipDate = order.getShipDate();
            LocalDateTime currentDate = LocalDateTime.now();
            long daysDifference = ChronoUnit.DAYS.between(shipDate, currentDate);
            int daysThreshold = 30;
            if (daysDifference >= daysThreshold) {
                order.setOrderStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
                System.out.println("Order " + order.getOrderId() + " automatically marked as completed. Return window closed.");
            }
        }
    }
}