package com.immiauto.enums;

/**
 * Lifecycle status shared by {@code CaseFormDraft} and {@code CasePackage}.
 */
public enum PackageStatus {
    DRAFT("Draft"),
    VALIDATION_FAILED("Validation Failed"),
    READY_FOR_APPROVAL("Ready for Approval"),
    APPROVED("Approved"),
    SUPERSEDED("Superseded");

    private final String displayName;

    PackageStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
