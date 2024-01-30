package com.startsteps.Final.Project.ECommerce.ProductManagement.models;

import jakarta.persistence.*;

@Entity
@Table(name = "products_orders",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"orderId", "productId"})
        })
public class ProductsOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productsOrderId;
    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;
    private Integer quantity;

    public ProductsOrders() {
    }

    public ProductsOrders(Order order, Product product, Integer quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }

    public Integer getProductsOrderId() {
        return productsOrderId;
    }

    public void setProductsOrderId(Integer productsOrderId) {
        this.productsOrderId = productsOrderId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
