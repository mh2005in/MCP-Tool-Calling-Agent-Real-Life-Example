package com.immiauto.enums;

public enum ApplicantRole {
    PRINCIPAL_APPLICANT("Principal Applicant"),
    SPOUSE("Spouse"),
    DEPENDENT_CHILD("Dependent Child"),
    SPONSOR("Sponsor"),
    EMPLOYER("Employer"),
    HOST("Host");

    private final String displayName;

    ApplicantRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
