package com.immiauto.enums;

public enum CaseSubtype {
    // Study Permit
    NEW_INTERNATIONAL_STUDENT("New International Student"),
    STUDENT_INSIDE_CANADA("Student Inside Canada - Extension/Change"),
    MINOR_STUDENT("Minor Student (K-12)"),
    QUEBEC_STUDENT("Quebec Student (CAQ)"),
    STUDENT_WITH_FAMILY("Student with Spouse/Family"),

    // Visitor Visa
    FAMILY_VISITOR("Family Visitor"),
    TOURISM("Tourism"),
    BUSINESS_VISITOR("Business Visitor"),
    EVENT_VISITOR("Event Visitor"),
    PREVIOUS_REFUSAL_VISITOR("Previous Refusal Applicant"),

    // Spousal Sponsorship
    SPOUSAL_INLAND("Spousal Sponsorship - Inland"),
    SPOUSAL_OUTLAND("Spousal Sponsorship - Outland"),
    COMMON_LAW("Common-Law Partner"),
    CONJUGAL("Conjugal Partner"),
    DEPENDENT_CHILD("Dependent Child Sponsorship"),

    // Express Entry
    FEDERAL_SKILLED_WORKER("Federal Skilled Worker"),
    CANADIAN_EXPERIENCE_CLASS("Canadian Experience Class"),
    FEDERAL_SKILLED_TRADES("Federal Skilled Trades"),
    PNP_EXPRESS_ENTRY("PNP Express Entry"),

    // Work Permit
    EMPLOYER_SPECIFIC_LMIA("Employer-Specific LMIA-Based"),
    EMPLOYER_SPECIFIC_EXEMPT("Employer-Specific LMIA-Exempt"),
    OPEN_WORK_PERMIT("Open Work Permit"),
    BRIDGING_OPEN_WORK_PERMIT("Bridging Open Work Permit"),
    INTRA_COMPANY_TRANSFER("Intra-Company Transfer"),
    INTERNATIONAL_MOBILITY("International Mobility Program"),

    // LMIA
    LMIA_HIGH_WAGE("LMIA High-Wage"),
    LMIA_LOW_WAGE("LMIA Low-Wage"),
    LMIA_PR_SUPPORT("LMIA PR Support"),
    LMIA_GLOBAL_TALENT("Global Talent Stream"),
    LMIA_AGRICULTURAL("Agricultural Stream"),
    LMIA_CAREGIVER("Caregiver Stream"),
    LMIA_SEASONAL("Seasonal Agricultural Worker"),

    // Citizenship
    ADULT_CITIZENSHIP_GRANT("Adult Citizenship Grant"),
    MINOR_CITIZENSHIP_GRANT("Minor Citizenship Grant"),
    CITIZENSHIP_CERTIFICATE("Citizenship Certificate"),
    REPLACEMENT_CERTIFICATE("Replacement Certificate"),

    // PR Card / PRTD
    PR_CARD_RENEWAL("PR Card Renewal"),
    PR_CARD_REPLACEMENT("PR Card Replacement"),
    PR_CARD_FIRST("PR Card First Application"),
    PR_TRAVEL_DOCUMENT("PR Travel Document"),

    // Super Visa
    SUPER_VISA_PARENT("Super Visa - Parent"),
    SUPER_VISA_GRANDPARENT("Super Visa - Grandparent"),

    // Generic
    OTHER_SUBTYPE("Other");

    private final String displayName;

    CaseSubtype(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
