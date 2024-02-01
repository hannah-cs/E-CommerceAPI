package com.startsteps.Final.Project.ECommerce.ProductManagement.controllers;


import com.startsteps.Final.Project.ECommerce.ProductManagement.models.Product;
import com.startsteps.Final.Project.ECommerce.ProductManagement.services.ProductService;
import com.startsteps.Final.Project.ECommerce.security.login.jwt.JwtUtils;
import com.startsteps.Final.Project.ECommerce.security.login.payload.response.MessageResponse;
import com.startsteps.Final.Project.ECommerce.security.login.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        return ResponseEntity.ok().body(new MessageResponse(productService.getAllProducts().toString()));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createProduct(HttpServletRequest request, @RequestBody Product newProduct) {
        productService.createProduct(newProduct);
        return ResponseEntity.ok().body(new MessageResponse("Product added to database."));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateProduct(HttpServletRequest request, @RequestBody Product updatedProduct){
        productService.updateProduct(updatedProduct.getProductId(), updatedProduct);
        return ResponseEntity.ok().body(new MessageResponse("Product updated."));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Integer id, HttpServletRequest request){
        productService.deleteProduct(id);
        return ResponseEntity.ok().body(new MessageResponse("Product deleted."));
    }
}
