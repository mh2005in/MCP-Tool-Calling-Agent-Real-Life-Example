package com.immiauto.dto.forms;

import lombok.*;

import java.util.UUID;

/**
 * Summary of a package profile for selection on the forms & package workspace.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PackageProfileSummaryDto {

    private UUID id;
    private String profileCode;
    private String displayName;
    private String serviceType;
    private String caseSubtype;
    private String jurisdiction;
    private String status;
    private String description;
    private int formCount;                // computed in service
    private int documentRequirementCount; // computed in service
}
