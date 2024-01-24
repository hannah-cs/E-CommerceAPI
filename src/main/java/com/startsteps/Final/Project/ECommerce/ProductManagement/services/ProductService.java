package com.startsteps.Final.Project.ECommerce.ProductManagement.services;

import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.ProductNotFoundException;
import com.startsteps.Final.Project.ECommerce.ProductManagement.models.Product;
import com.startsteps.Final.Project.ECommerce.ProductManagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Transactional
    public Product loadProductById(Integer productId) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        return optionalProduct.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
    }

}
