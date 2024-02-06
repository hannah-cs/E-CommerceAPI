package com.startsteps.Final.Project.ECommerce.OrderManagement.controller;


import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.InvalidOrderStateException;
import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.OrderNotFoundException;
import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.ProductNotFoundException;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.Order;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.OrderStatus;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.ProductsOrders;
import com.startsteps.Final.Project.ECommerce.OrderManagement.payload.CartRequest;
import com.startsteps.Final.Project.ECommerce.OrderManagement.payload.CartResponse;
import com.startsteps.Final.Project.ECommerce.OrderManagement.service.OrderService;
import com.startsteps.Final.Project.ECommerce.ProductManagement.services.ProductService;
import com.startsteps.Final.Project.ECommerce.security.login.jwt.JwtUtils;
import com.startsteps.Final.Project.ECommerce.security.login.models.User;
import com.startsteps.Final.Project.ECommerce.security.login.payload.response.MessageResponse;
import com.startsteps.Final.Project.ECommerce.security.login.repository.UserRepository;
import com.startsteps.Final.Project.ECommerce.security.login.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    UserRepository userRepository;


    @Autowired
    OrderService orderService;


    @Autowired
    JwtUtils jwtUtils;


    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok().body(new MessageResponse(orders.getContent().toString()));
    }


    //accepts status param e.g. ?status=PROCESSING to filter orders by status. returns all if none propvided
    @GetMapping
    public ResponseEntity<?> getMyOrders(
            HttpServletRequest request,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        int userId = jwtUtils.getUserIdFromJwtToken(jwtUtils.getJwtFromCookies(request));
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;
        if (status != null) {
            orders = orderService.loadUserOrdersWithStatus(userId, status, pageable);
        } else {
            orders = orderService.loadOrdersByUser(userId, pageable);
        }

        return ResponseEntity.ok().body(new MessageResponse(orders.getContent().toString()));
    }


    @PutMapping("/checkout")
    public ResponseEntity<?> checkoutCart(HttpServletRequest request) {
        int userId = jwtUtils.getUserIdFromJwtToken(jwtUtils.getJwtFromCookies(request));
        if (orderService.hasCart(userId)) {
            Order cart = orderService.viewCart(userId);
            if (cart != null) {
                int cartId = cart.getOrderId();
                orderService.checkoutOrder(cartId);
                return ResponseEntity.ok().body(new MessageResponse("Order placed successfully."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new MessageResponse("Error processing the order."));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Add items to cart before checking out."));
        }
    }



    @PostMapping("/add")
    public ResponseEntity<?> addToCart(HttpServletRequest request, @RequestBody CartRequest cartRequest) {
        try {
            int userId = jwtUtils.getUserIdFromJwtToken(jwtUtils.getJwtFromCookies(request));
            orderService.addToCart(userId, cartRequest.getProductId(), cartRequest.getQuantity());
            return ResponseEntity.ok().body(new MessageResponse("Product added to the cart successfully."));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("No product found with product id "+cartRequest.getProductId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error adding product to cart."));
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeFromCart(HttpServletRequest request, @RequestBody CartRequest cartRequest) {
        try {
            int userId = jwtUtils.getUserIdFromJwtToken(jwtUtils.getJwtFromCookies(request));
            orderService.removeFromCart(userId, cartRequest.getProductId());
            return ResponseEntity.ok().body(new MessageResponse("Product removed from cart successfully."));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Product id "+cartRequest.getProductId()+" doesn't exist or is not in cart"));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error removing product from the cart."));
        }
    }

    @GetMapping("/cart")
    public ResponseEntity<?> getCart(HttpServletRequest request) {
        int userId = jwtUtils.getUserIdFromJwtToken(jwtUtils.getJwtFromCookies(request));
        Order cart = orderService.viewCart(userId);

        if (cart != null) {
            CartResponse cartResponse = new CartResponse("In cart:", cart.getProductsOrders());
            String cartItemsString = cartResponse.toString();
            return ResponseEntity.ok().body(new MessageResponse(cartItemsString));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(
            @PathVariable int orderId,
            @RequestBody Order updatedOrder,
            HttpServletRequest request
    ) {
        int userId = jwtUtils.getUserIdFromJwtToken(jwtUtils.getJwtFromCookies(request));
        Order order = orderService.loadOrderById(orderId);
        if (order.getUserId()!=userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You are not authorized to update this order."));
        }
        try {
            orderService.updateOrder(orderId, updatedOrder);
            return ResponseEntity.ok().body(new MessageResponse("Order updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error updating the order."));
        }
    }
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable int orderId,
            HttpServletRequest request
    ) {
        int userId = jwtUtils.getUserIdFromJwtToken(jwtUtils.getJwtFromCookies(request));
        Order order = orderService.loadOrderById(orderId);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("No order found with id " + orderId));
        }
        if (order.getUserId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You are not authorized to cancel this order."));
        }
        try {
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok().body(new MessageResponse("Order cancelled successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error cancelling the order."));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/{orderId}/ship")
    public ResponseEntity<?> shipOrder(@PathVariable int orderId) {
        try {
            orderService.shipOrder(orderId);
            return ResponseEntity.ok().body(new MessageResponse("Order shipped successfully."));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("No order found with id " + orderId));
        } catch (InvalidOrderStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: order cancelled or already shipped"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error shipping the order."));
        }
    }

    @PostMapping("/{orderId}/return")
    public ResponseEntity<?> returnOrder(@PathVariable int orderId, HttpServletRequest request) {
        int userId = jwtUtils.getUserIdFromJwtToken(jwtUtils.getJwtFromCookies(request));
        Order order = orderService.loadOrderById(orderId);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("No order found with id " + orderId));
        }
        if (order.getUserId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You are not authorized to return this order."));
        }
        try {
            orderService.returnOrder(orderId);
            return ResponseEntity.ok().body(new MessageResponse("Return process successfully started."));
        } catch (InvalidOrderStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error returning the order."));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable int orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok().body(new MessageResponse("Order deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error deleting the order."));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Order newOrder) {
        try {
            orderService.createOrder(newOrder);
            return ResponseEntity.ok().body(new MessageResponse("Order created successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error creating the order."));
        }
    }


}