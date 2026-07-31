package com.immiauto.dto.forms;

import lombok.*;

import java.util.UUID;

/**
 * Outcome of inspecting a form's governed source PDF and syncing its fields
 * (Section 4.1 - Milestone 6). Drives supportsFill / status and thus whether
 * the workspace offers auto-fill or manual upload for the form.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FormInspectionResultDto {
    private UUID formId;
    private String formCode;
    private boolean hasAcroForm;
    private boolean hasXfa;
    private int pageCount;
    private int fieldCount;
    private String sha256;
    private String classification; // STANDARD_ACROFORM, DYNAMIC_XFA, NO_FORM_FIELDS
    private boolean supportsFill;
    private String status;         // resulting FormDefinition status
    private String message;
}
