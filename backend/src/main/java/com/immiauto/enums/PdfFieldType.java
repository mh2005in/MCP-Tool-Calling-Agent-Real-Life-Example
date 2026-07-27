package com.immiauto.enums;

public enum PdfFieldType {
    TEXT("Text"),
    CHECKBOX("Checkbox"),
    RADIO("Radio"),
    DATE("Date"),
    DROPDOWN("Dropdown"),
    SIGNATURE("Signature"),
    BARCODE("Barcode"),
    UNSUPPORTED("Unsupported");

    private final String displayName;

    PdfFieldType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
