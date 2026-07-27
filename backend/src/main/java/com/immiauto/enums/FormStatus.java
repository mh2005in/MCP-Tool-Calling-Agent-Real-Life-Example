package com.immiauto.enums;

public enum FormStatus {
    DRAFT("Draft"),
    ACTIVE("Active"),
    RETIRED("Retired"),
    BLOCKED("Blocked");

    private final String displayName;

    FormStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
