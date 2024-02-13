package com.startsteps.Final.Project.ECommerce.OrderManagement.models;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "orderId"),
        })
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    private LocalDateTime orderDate;
    private LocalDateTime shipDate;

    private Integer userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<ProductsOrders> productsOrders = new ArrayList<>();

    public Order() {
    }

    public Order(Integer userId, OrderStatus orderStatus) {
        this.userId = userId;
        this.orderStatus = orderStatus;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getShipDate() {
        return shipDate;
    }

    public void setShipDate(LocalDateTime shipDate) {
        this.shipDate = shipDate;
    }

    public List<ProductsOrders> getProductsOrders() {
        return productsOrders;
    }

    public void setProductsOrders(List<ProductsOrders> productsOrders) {
        this.productsOrders = productsOrders;
    }

    public void addProductOrder(ProductsOrders productsOrder) {
        this.productsOrders.add(productsOrder);
        productsOrder.setOrder(this);
    }

    public void removeProductOrder(ProductsOrders productsOrder) {
        this.productsOrders.remove(productsOrder);
    }

    public double calculateTotalPrice() {
        if (productsOrders == null || productsOrders.isEmpty()) {
            return 0.0;
        }

        double totalPrice = 0.0;
        for (ProductsOrders po : productsOrders) {
            totalPrice += po.getProduct().getUnitPrice() * po.getQuantity();
        }

        return totalPrice;
    }

    @Override
    public String toString(){
        return "Order number "+orderId+" placed "+orderDate+
                "\nOrder status: "+orderStatus.toString()+
                "\nContaining: "+
                productsOrders;
    }
}
