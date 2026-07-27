package com.immiauto.exception;

public class AdminAccessRequiredException extends RuntimeException {

    public AdminAccessRequiredException(String message) {
        super(message);
    }
}
