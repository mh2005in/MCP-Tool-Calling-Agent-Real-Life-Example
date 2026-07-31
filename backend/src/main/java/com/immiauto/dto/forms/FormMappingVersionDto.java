package com.immiauto.dto.forms;

import lombok.*;

import java.util.UUID;

/**
 * A mapping version for a form (Section 4.1 - Milestone 6).
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FormMappingVersionDto {
    private UUID id;
    private UUID formDefinitionId;
    private Integer mappingVersion;
    private String status;
    private String approvedByConsultantName;
    private String approvedAt;
    private String changeSummary;
    private String notes;
}
