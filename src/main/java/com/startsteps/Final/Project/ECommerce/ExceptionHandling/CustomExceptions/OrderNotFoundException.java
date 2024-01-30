package com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }
}
