package com.startsteps.Final.Project.ECommerce.ExceptionHandling.CustomExceptions;

public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
