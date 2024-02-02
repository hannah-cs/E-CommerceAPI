package com.startsteps.Final.Project.ECommerce.OrderManagement.payload;

import com.startsteps.Final.Project.ECommerce.OrderManagement.models.ProductsOrders;

import java.text.DecimalFormat;
import java.util.List;

public class CartResponse {
    private String message;
    private List<ProductsOrders> cartItems;

    public CartResponse(String message, List<ProductsOrders> cartItems) {
        this.message = message;
        this.cartItems = cartItems;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ProductsOrders> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<ProductsOrders> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(message + " ");

        double totalAmount = 0.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        for (ProductsOrders item : cartItems) {
            double itemAmount = item.getProduct().getUnitPrice() * item.getQuantity();
            totalAmount += itemAmount;

            result.append(item.getProduct().getProductName())
                    .append(", €")
                    .append(item.getProduct().getUnitPrice())
                    .append(" x")
                    .append(item.getQuantity())
                    .append(" (€")
                    .append(decimalFormat.format(itemAmount))
                    .append("); ")
                    .append("\n");
        }

        String formattedTotalAmount = decimalFormat.format(totalAmount);

        result.append("Total: €").append(formattedTotalAmount);
        return result.toString().trim();
    }


}
