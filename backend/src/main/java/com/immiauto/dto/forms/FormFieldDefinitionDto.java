package com.immiauto.dto.forms;

import lombok.*;

import java.util.UUID;

/**
 * A PDF field discovered on a form (Section 4.1 - Milestone 6).
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FormFieldDefinitionDto {
    private UUID id;
    private String pdfFieldName;
    private String label;
    private String fieldType;
    private boolean required;
    private Integer maxLength;
    private String allowedValues;
    private Integer pageNumber;
    private boolean readOnly;
    private boolean calculated;
    private String notes;
}
