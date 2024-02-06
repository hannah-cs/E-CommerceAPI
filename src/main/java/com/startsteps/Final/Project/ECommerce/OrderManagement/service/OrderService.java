package com.startsteps.Final.Project.ECommerce.OrderManagement.service;

import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.InvalidOrderStateException;
import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.OrderNotFoundException;
import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.ProductNotFoundException;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.Order;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.OrderStatus;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.ProductsOrders;
import com.startsteps.Final.Project.ECommerce.OrderManagement.repository.OrderRepository;
import com.startsteps.Final.Project.ECommerce.ProductManagement.models.Product;
import com.startsteps.Final.Project.ECommerce.ProductManagement.repository.ProductRepository;
import com.startsteps.Final.Project.ECommerce.security.login.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Autowired
    ProductRepository productRepository;
    @Transactional
    public Order loadOrderById(Integer orderId) throws OrderNotFoundException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        return optionalOrder.orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
    }

    public Page<Order> loadOrdersByUser(Integer userId, Pageable pageable) throws OrderNotFoundException {
        return orderRepository.findByUserId(userId, pageable);
    }

    public Page<Order> loadUserOrdersWithStatus(Integer userId, OrderStatus status, Pageable pageable) throws OrderNotFoundException {
        return orderRepository.findByUserIdAndOrderStatus(userId, status, pageable);
    }

    public Order viewCart(Integer userId) {
        return orderRepository.findFirstByUserIdAndOrderStatus(userId, OrderStatus.IN_CART)
                .orElse(null);
    }


    public boolean hasCart(Integer userId) {
        return orderRepository.findFirstByUserIdAndOrderStatus(userId, OrderStatus.IN_CART)
                .isPresent();
    }

    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public void createOrder(Order newOrder){
        orderRepository.save(newOrder);
    }

    @Transactional
    public void updateOrder(int id, Order updatedOrder){
        Order order = orderRepository.findById(id).orElse(null);
        order.setOrderDate(updatedOrder.getOrderDate());
        order.setOrderStatus(updatedOrder.getOrderStatus());
        orderRepository.save(order);
    }

    public void deleteOrder(int id){
        orderRepository.deleteById(id);
    }

    @Transactional
    public void addToCart(int userId, int productId, int quantity) {
        Order existingOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.IN_CART)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    Order newOrder = new Order(userId, OrderStatus.IN_CART);
                    orderRepository.save(newOrder);
                    return newOrder;
                });
        ProductsOrders existingProductOrder = existingOrder.getProductsOrders().stream()
                .filter(po -> po.getProduct().getProductId() == productId)
                .findFirst()
                .orElse(null);
        if (existingProductOrder != null) {
            existingProductOrder.setQuantity(existingProductOrder.getQuantity() + quantity);
        } else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found"));
            ProductsOrders newProductOrder = new ProductsOrders(existingOrder, product, quantity);
            existingOrder.addProductOrder(newProductOrder);
        }
        orderRepository.save(existingOrder);
    }


    @Transactional
    public void removeFromCart(int userId, int productId) {
        Order existingOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.IN_CART)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No cart found for this user"));
        ProductsOrders productsOrders = existingOrder.getProductsOrders().stream()
                .filter(po -> po.getProduct().getProductId() == productId)
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product not found in cart"));
        existingOrder.removeProductOrder(productsOrders);
        orderRepository.save(existingOrder);
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
                orderRepository.save(order);
                System.out.println("Order cancelled successfully.");
            }
        }
    }

    public void returnOrder(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new OrderNotFoundException("No order found with id " + orderId);
        } else {
            if (order.getOrderStatus() == OrderStatus.SHIPPED) {
                order.setOrderStatus(OrderStatus.RETURNED);
                orderRepository.save(order);
            } else if (order.getOrderStatus() == OrderStatus.COMPLETED) {
                throw new InvalidOrderStateException("Error: return window exceeded");
            } else if (order.getOrderStatus() == OrderStatus.CANCELLED) {
                throw new InvalidOrderStateException("Error: this is a cancelled order");
            } else if (order.getOrderStatus() == OrderStatus.RETURNED) {
                throw new InvalidOrderStateException("This order is already in the return process.");
            } else if (order.getOrderStatus() == OrderStatus.PROCESSING) {
                throw new InvalidOrderStateException("This order has not yet been shipped. Please cancel instead.");
            } else {
                throw new InvalidOrderStateException("Error: return could not be processed. Double-check order status.");
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
                orderRepository.save(order);
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

    public void checkoutOrder(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order != null && order.getOrderStatus() == OrderStatus.IN_CART) {
            order.setOrderStatus(OrderStatus.PROCESSING);
            order.setOrderDate(LocalDateTime.now());
            orderRepository.save(order);
        }
    }
}
