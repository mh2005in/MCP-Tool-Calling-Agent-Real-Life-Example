package com.immiauto.enums;

public enum ValidationSeverity {
    ERROR("Error"),
    WARNING("Warning"),
    DECISION("Consultant Decision"),
    CLIENT_CONFIRMATION("Client Confirmation"),
    UNRESOLVED_EVIDENCE("Unresolved Evidence");

    private final String displayName;

    ValidationSeverity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
