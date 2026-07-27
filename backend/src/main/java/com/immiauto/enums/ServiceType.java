package com.immiauto.enums;

public enum ServiceType {
    STUDY_PERMIT("Study Permit"),
    VISITOR_VISA("Visitor Visa"),
    SPOUSAL_SPONSORSHIP("Spousal Sponsorship"),
    EXPRESS_ENTRY("Express Entry"),
    WORK_PERMIT("Work Permit"),
    LMIA("LMIA"),
    CITIZENSHIP("Citizenship"),
    PGWP("Post-Graduation Work Permit"),
    SUPER_VISA("Super Visa"),
    PR_CARD_PRTD("PR Card / PRTD"),
    PNP("Provincial Nominee Program"),
    OTHER("Other");

    private final String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
