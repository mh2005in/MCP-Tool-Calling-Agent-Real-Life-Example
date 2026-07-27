package com.immiauto.dto.forms;

import lombok.*;

import java.time.LocalDate;

/**
 * Full package profile for admin management (Section 4.1 - Milestone 6).
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PackageProfileDto {
    private Long id;
    private String profileCode;
    private String displayName;
    private String serviceType;
    private String caseSubtype;
    private String jurisdiction;
    private String status;
    private String description;
    private LocalDate effectiveDate;
    private LocalDate retirementDate;
    private int formCount;
    private int documentRequirementCount;
}
