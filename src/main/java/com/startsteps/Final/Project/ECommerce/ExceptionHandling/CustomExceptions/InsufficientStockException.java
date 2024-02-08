package com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
