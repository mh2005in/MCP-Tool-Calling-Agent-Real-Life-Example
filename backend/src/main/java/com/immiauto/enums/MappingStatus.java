package com.immiauto.enums;

public enum MappingStatus {
    DRAFT("Draft"),
    IN_REVIEW("In Review"),
    APPROVED("Approved"),
    RETIRED("Retired");

    private final String displayName;

    MappingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
