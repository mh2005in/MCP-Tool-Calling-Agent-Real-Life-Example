package com.immiauto.enums;

/**
 * Issuing jurisdiction for a governed form or package profile.
 * FEDERAL covers IRCC forms; provincial values cover PNP / provincial programs.
 */
public enum Jurisdiction {
    FEDERAL("Federal (IRCC)"),
    ONTARIO("Ontario"),
    QUEBEC("Quebec"),
    BRITISH_COLUMBIA("British Columbia"),
    ALBERTA("Alberta"),
    MANITOBA("Manitoba"),
    SASKATCHEWAN("Saskatchewan"),
    NOVA_SCOTIA("Nova Scotia"),
    NEW_BRUNSWICK("New Brunswick"),
    NEWFOUNDLAND_LABRADOR("Newfoundland and Labrador"),
    PRINCE_EDWARD_ISLAND("Prince Edward Island"),
    NORTHWEST_TERRITORIES("Northwest Territories"),
    YUKON("Yukon"),
    NUNAVUT("Nunavut"),
    OTHER("Other");

    private final String displayName;

    Jurisdiction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
