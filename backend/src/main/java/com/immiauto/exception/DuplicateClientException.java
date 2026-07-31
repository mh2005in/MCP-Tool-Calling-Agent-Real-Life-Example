package com.immiauto.exception;

import java.util.UUID;

public class DuplicateClientException extends RuntimeException {

    private final UUID existingClientId;

    public DuplicateClientException(String message, UUID existingClientId) {
        super(message);
        this.existingClientId = existingClientId;
    }

    public UUID getExistingClientId() {
        return existingClientId;
    }
}
