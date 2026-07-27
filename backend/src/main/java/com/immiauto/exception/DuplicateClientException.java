package com.immiauto.exception;

public class DuplicateClientException extends RuntimeException {

    private final Long existingClientId;

    public DuplicateClientException(String message, Long existingClientId) {
        super(message);
        this.existingClientId = existingClientId;
    }

    public Long getExistingClientId() {
        return existingClientId;
    }
}
