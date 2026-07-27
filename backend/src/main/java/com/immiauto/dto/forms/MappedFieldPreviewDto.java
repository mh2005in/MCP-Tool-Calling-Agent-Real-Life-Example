package com.immiauto.dto.forms;

import lombok.*;

/**
 * Preview of a single PDF field after applying a mapping rule to the
 * canonical snapshot.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MappedFieldPreviewDto {

    private String pdfFieldName;
    private String label;
    private String fieldType;
    private String canonicalFieldKey;
    private String transformType;
    private String resolvedValue;          // value that would be written to the PDF
    private boolean present;               // a non-blank value was resolved
    private boolean requiredForPackage;
    private boolean consultantReviewRequired;
    private String status;                 // MAPPED, MISSING_VALUE, REVIEW_REQUIRED, NO_SOURCE, UNSUPPORTED_TRANSFORM
    private String sourceSummary;          // short provenance summary
    private String diagnostic;             // explanation when not cleanly mapped
}
