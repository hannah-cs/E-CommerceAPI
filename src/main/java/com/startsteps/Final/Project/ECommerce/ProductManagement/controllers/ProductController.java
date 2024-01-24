package com.startsteps.Final.Project.ECommerce.ProductManagement.controllers;


import com.startsteps.Final.Project.ECommerce.ProductManagement.models.Product;
import com.startsteps.Final.Project.ECommerce.ProductManagement.services.ProductService;
import com.startsteps.Final.Project.ECommerce.security.login.WebSecurityConfig;
import com.startsteps.Final.Project.ECommerce.security.login.jwt.JwtUtils;
import com.startsteps.Final.Project.ECommerce.security.login.models.User;
import com.startsteps.Final.Project.ECommerce.security.login.payload.response.MessageResponse;
import com.startsteps.Final.Project.ECommerce.security.login.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getAllProducts(HttpServletRequest request){
        String jwt = jwtUtils.getJwtFromCookies(request);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            System.out.println(username);
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null){
                return ResponseEntity.ok().body(new MessageResponse(productService.getAllProducts().toString()));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("You must be an admin to perform this action."));
            }
        }
        return ResponseEntity.badRequest().body(new MessageResponse("You must be logged in to access this feature."));
    }

    @PostMapping
    public ResponseEntity<?> createProduct(HttpServletRequest request, @RequestBody Product newProduct){
        String jwt = jwtUtils.getJwtFromCookies(request);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            System.out.println(username);
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null){
                productService.createProduct(newProduct);
                return ResponseEntity.ok().body(new MessageResponse("Product added to database."));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("You must be an admin to perform this action."));
            }
        }
        return ResponseEntity.badRequest().body(new MessageResponse("You must be logged in to access this feature."));
    }
}
