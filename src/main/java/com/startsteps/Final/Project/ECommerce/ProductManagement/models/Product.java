package com.startsteps.Final.Project.ECommerce.ProductManagement.models;

import jakarta.persistence.*;

@Entity
@Table(name = "products",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "productId"),
        })
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;
    private String productName;
    private double unitPrice;
    private Integer stockCount;
    private String description;

    public Product() {
    }

    public Product(String productName, double unitPrice, Integer stockCount, String description) {
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.stockCount = stockCount;
        this.description = description;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString(){
        return "{Product "+productId+": "+productName + ", " +description +
                ". " +stockCount+" in stock at €"+unitPrice+" each. Total stock value €"+(unitPrice*stockCount)+"}";
    }
}
