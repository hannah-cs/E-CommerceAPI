package com.startsteps.Final.Project.ECommerce.ProductManagement.services;

import com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions.ProductNotFoundException;
import com.startsteps.Final.Project.ECommerce.ProductManagement.models.Product;
import com.startsteps.Final.Project.ECommerce.ProductManagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public void createProduct(Product newProduct){
        productRepository.save(newProduct);
    }

    public void updateProduct(int id, Product updatedProduct){
        Product product = productRepository.findById(id).orElse(null);
        product.setProductName(updatedProduct.getProductName());
        product.setStockCount(updatedProduct.getStockCount());
        product.setDescription(updatedProduct.getDescription());
        product.setUnitPrice(updatedProduct.getUnitPrice());
    }

    public void deleteProduct(int id){
        productRepository.deleteById(id);
    }

}
