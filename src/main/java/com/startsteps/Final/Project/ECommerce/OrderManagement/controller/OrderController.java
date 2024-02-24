package com.startsteps.Final.Project.ECommerce.OrderManagement.controller;


import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.InsufficientStockException;
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
import com.startsteps.Final.Project.ECommerce.security.login.services.UserDetailsImpl;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getAllOrders(page, size);
    }





    //accepts status param e.g. ?status=PROCESSING to filter orders by status. returns all if none propvided
    @GetMapping
    public ResponseEntity<?> getMyOrders(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        int userId = userDetails.getId();

        return orderService.getMyOrders(userId, status, page, size);
    }


    @PutMapping("/checkout")
    public ResponseEntity<?> checkoutCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        int userId = userDetails.getId();
        return orderService.checkoutOrder(userId);
    }



    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest cartRequest, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getId();

        try {
            orderService.addToCart(userId, cartRequest.getProductId(), cartRequest.getQuantity());
            return ResponseEntity.ok().body(new MessageResponse("Product added to the cart successfully."));
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("No product found with product id " + cartRequest.getProductId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error adding product to cart."));
        }
    }


    @PostMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestBody CartRequest cartRequest, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            int userId = userDetails.getId();
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
    public ResponseEntity<?> getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getId();
        ResponseEntity<?> responseEntity = orderService.getCart(userId);
        return responseEntity;
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(
            @PathVariable int orderId,
            @RequestBody Order updatedOrder
    ) {
        ResponseEntity<?> responseEntity = orderService.updateOrder(orderId, updatedOrder);
        return responseEntity;
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable int orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        int userId = userDetails.getId();
        ResponseEntity<?> responseEntity = orderService.cancelOrder(orderId, userId);
        return responseEntity;
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
    public ResponseEntity<?> returnOrder(@PathVariable int orderId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        int userId = userDetails.getId();
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
            return ResponseEntity.ok().body(new MessageResponse("Return process successfully started. Your refund of €"+order.calculateTotalPrice()+" will be credited to the payment method you used when purchasing the returned items. It may take up to 5 working days to clear depending on your bank."));
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
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("No order found with id "+orderId+". It may have already been deleted."));
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