package com.immiauto.dto.forms;

import lombok.*;

import java.time.LocalDate;

/**
 * Governed form catalogue entry (Section 4.1 - Milestone 6).
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FormDefinitionDto {

    private Long id;
    private String formCode;
    private String displayName;
    private String jurisdiction;
    private String programCategory;
    private String sourceUrl;
    private String sourceFileName;
    private String sourceSha256;
    private LocalDate effectiveDate;
    private LocalDate retirementDate;
    private String editionLabel;
    private boolean supportsFill;
    private boolean supportsBarcode;
    private String status;
    private String notes;
    private int fieldCount; // computed
}
