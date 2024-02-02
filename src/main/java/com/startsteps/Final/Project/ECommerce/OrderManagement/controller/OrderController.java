package com.startsteps.Final.Project.ECommerce.OrderManagement.controller;

import com.startsteps.Final.Project.ECommerce.OrderManagement.models.Order;
import com.startsteps.Final.Project.ECommerce.OrderManagement.models.OrderStatus;
import com.startsteps.Final.Project.ECommerce.OrderManagement.service.OrderService;
import com.startsteps.Final.Project.ECommerce.ProductManagement.services.ProductService;
import com.startsteps.Final.Project.ECommerce.security.login.jwt.JwtUtils;
import com.startsteps.Final.Project.ECommerce.security.login.models.User;
import com.startsteps.Final.Project.ECommerce.security.login.payload.response.MessageResponse;
import com.startsteps.Final.Project.ECommerce.security.login.repository.UserRepository;
import com.startsteps.Final.Project.ECommerce.security.login.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<?> getAllOrders(HttpServletRequest request){
        return ResponseEntity.ok().body(new MessageResponse(orderService.getAllOrders().toString()));
    }

    //accepts status param e.g. ?status=PROCESSING to filter orders by status. returns all if none propvided
    @GetMapping
    public ResponseEntity<?> getMyOrders(
            HttpServletRequest request,
            @RequestParam(required = false) OrderStatus status) {
        int userId = jwtUtils.getUserIdFromJwtToken(jwtUtils.getJwtFromCookies(request));
        List<Order> orders;
        if (status != null) {
            orders = orderService.loadUserOrdersWithStatus(userId, status);
        } else {
            orders = orderService.loadOrdersByUser(userId);
        }

        return ResponseEntity.ok().body(new MessageResponse(orders.toString()));
    }
}
