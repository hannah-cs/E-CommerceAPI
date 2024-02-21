package com.startsteps.Final.Project.ECommerce.OrderManagement.service;

import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.InsufficientStockException;
import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.InvalidOrderStateException;
import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.OrderNotFoundException;
import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.ProductNotFoundException;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.Order;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.OrderStatus;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.ProductsOrders;
import com.startsteps.Final.Project.ECommerce.OrderManagement.repository.OrderRepository;
import com.startsteps.Final.Project.ECommerce.OrderManagement.repository.ProductsOrdersRepository;
import com.startsteps.Final.Project.ECommerce.ProductManagement.models.Product;
import com.startsteps.Final.Project.ECommerce.ProductManagement.repository.ProductRepository;
import com.startsteps.Final.Project.ECommerce.security.login.models.User;
import com.startsteps.Final.Project.ECommerce.security.login.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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

    public ResponseEntity<?> getMyOrders(int userId, OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;

        if (status != null) {
            orders = orderRepository.findByUserIdAndOrderStatus(userId, status, pageable);
        } else {
            orders = orderRepository.findByUserId(userId, pageable);
        }

        List<Map<String, Object>> orderList = orders.getContent().stream()
                .map(order -> {
                    Map<String, Object> orderJson = new HashMap<>();
                    orderJson.put("orderNumber", order.getOrderId());
                    orderJson.put("orderDate", order.getOrderDate());
                    orderJson.put("orderStatus", order.getOrderStatus());
                    orderJson.put("products", order.getProductsOrders().toString());
                    String formattedTotalPrice = String.format("%.2f", order.calculateTotalPrice());
                    orderJson.put("totalPrice", formattedTotalPrice);
                    if (status == null) {
                        orderJson.put("shipDate:", order.getShipDate());
                    }
                    return orderJson;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(orderList);
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

    public ResponseEntity<?> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findAll(pageable);

        List<Map<String, Object>> orderList = orders.getContent().stream()
                .map(order -> {
                    Map<String, Object> orderJson = new HashMap<>();
                    orderJson.put("orderNumber", order.getOrderId());
                    orderJson.put("user", order.getUserId());
                    orderJson.put("orderDate", order.getOrderDate());
                    orderJson.put("orderStatus", order.getOrderStatus());
                    orderJson.put("products", order.getProductsOrders().toString());
                    String formattedTotalPrice = String.format("%.2f", order.calculateTotalPrice());
                    orderJson.put("totalPrice", formattedTotalPrice);
                    return orderJson;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(orderList);
    }

    public void createOrder(Order newOrder){
        orderRepository.save(newOrder);
    }

    @Transactional
    public ResponseEntity<?> updateOrder(int id, Order updatedOrder) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Order not found."));
        }
        try {
            order.setOrderDate(updatedOrder.getOrderDate());
            order.setOrderStatus(updatedOrder.getOrderStatus());
            orderRepository.save(order);
            return ResponseEntity.ok().body(new MessageResponse("Order updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error updating the order."));
        }
    }

    public void deleteOrder(int id) throws OrderNotFoundException {
        if (orderRepository.existsById(id)){
            orderRepository.deleteById(id);
        } else {
            throw new OrderNotFoundException("No order found with id "+id);
        }
    }

    @Transactional
    public void addToCart(int userId, int productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        if (product.getStockCount() < quantity) {
            throw new InsufficientStockException("Insufficient stock for " + product.getProductName()+". Only "+product.getStockCount()+" available.");
        }

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
            if (existingProductOrder.getQuantity() > 1) {
                existingProductOrder.setQuantity(existingProductOrder.getQuantity() - 1);
            } else {
                existingOrder.removeProductOrder(existingProductOrder);
            }
        }

        orderRepository.save(existingOrder);
    }





    @Transactional
    public ResponseEntity<?> cancelOrder(int orderId, int userId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("No order found with id " + orderId));
        }
        if (order.getUserId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You are not authorized to cancel this order."));
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            return ResponseEntity.ok().body(new MessageResponse("Order already cancelled."));
        }
        if (order.getOrderStatus() == OrderStatus.RETURNED) {
            return ResponseEntity.ok().body(new MessageResponse("This order has been returned and may not be cancelled."));
        }
        if (order.getOrderStatus() == OrderStatus.SHIPPED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Order has already been shipped. Cannot be cancelled."));
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return ResponseEntity.ok().body(new MessageResponse("Order cancelled successfully."));
    }

    public void returnOrder(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new OrderNotFoundException("No order found with id " + orderId);
        } else {
            if (order.getOrderStatus() == OrderStatus.SHIPPED) {
                for (ProductsOrders productsOrders : order.getProductsOrders()) {
                    Product product = productsOrders.getProduct();
                    int returnedQuantity = productsOrders.getQuantity();
                    product.setStockCount(product.getStockCount() + returnedQuantity); // Increase the stock count
                    productRepository.save(product);
                }
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
    public void shipOrder(int orderId) throws OrderNotFoundException, InvalidOrderStateException {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null){
            throw new OrderNotFoundException("No order found with id " + orderId);
        } else {
            if (order.getOrderStatus() == OrderStatus.PROCESSING){
                order.setOrderStatus(OrderStatus.SHIPPED);
                order.setShipDate(LocalDateTime.now());
                orderRepository.save(order);
                System.out.println("Order shipped successfully.");
            } else {
                throw new InvalidOrderStateException("Error: order cancelled or already shipped");
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
            int daysThreshold = 100;
            if (daysDifference >= daysThreshold) {
                order.setOrderStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
            }
        }
    }

    @Transactional
    public ResponseEntity<?> checkoutOrder(int userId) {
        Order cart = viewCart(userId);
        if (cart == null || cart.getOrderStatus() != OrderStatus.IN_CART) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Add items to cart before checking out."));
        }

        List<ProductsOrders> productOrders = cart.getProductsOrders();
        for (ProductsOrders po : productOrders) {
            Product product = po.getProduct();
            int orderedQuantity = po.getQuantity();
            int availableQuantity = product.getStockCount();

            if (orderedQuantity > availableQuantity) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getProductName());
            }
            product.setStockCount(availableQuantity - orderedQuantity);
            productRepository.save(product);
        }

        cart.setOrderStatus(OrderStatus.PROCESSING);
        cart.setOrderDate(LocalDateTime.now());
        orderRepository.save(cart);

        return ResponseEntity.ok().body(new MessageResponse("Order placed successfully."));
    }

    public ResponseEntity<?> getCart(Integer userId) {
        Order cart = orderRepository.findFirstByUserIdAndOrderStatus(userId, OrderStatus.IN_CART)
                .orElse(null);
        if (cart != null) {
            List<Map<String, Object>> items = new ArrayList<>();
            double totalPrice = 0.0;

            for (ProductsOrders po : cart.getProductsOrders()) {
                Map<String, Object> item = new HashMap<>();
                item.put("productName", po.getProduct().getProductName());
                item.put("unitPrice", po.getProduct().getUnitPrice());
                item.put("quantity", po.getQuantity());
                double itemTotalPrice = po.getProduct().getUnitPrice() * po.getQuantity();
                item.put("subtotal", String.format("%.2f", itemTotalPrice));
                items.add(item);
                totalPrice += itemTotalPrice;
            }

            String formattedTotalPrice = String.format("%.2f", totalPrice);

            Map<String, Object> response = new HashMap<>();
            response.put("totalPrice", formattedTotalPrice);
            response.put("items", items);

            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.ok().body("You have no items in your cart.");
        }
    }


}
