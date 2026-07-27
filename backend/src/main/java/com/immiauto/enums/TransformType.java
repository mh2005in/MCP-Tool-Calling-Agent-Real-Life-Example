package com.immiauto.enums;

/**
 * Deterministic transforms applied when mapping a canonical value to a PDF field.
 * Arbitrary code transforms are intentionally not supported; {@link #CUSTOM} refers
 * to a named Java transform resolved by key.
 */
public enum TransformType {
    DIRECT("Direct Value"),
    DATE_FORMAT("Date Format"),
    CONCAT("Concatenate"),
    SPLIT_NAME("Split Name"),
    CHECKBOX_BOOLEAN("Boolean to Checkbox"),
    ENUM_MAP("Enum Map"),
    LIST_ROW("List Row"),
    DEFAULT_VALUE("Default Value"),
    CUSTOM("Custom (named Java transform)");

    private final String displayName;

    TransformType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
