package com.immiauto.dto.forms;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Ordered index of a submission package: the forms and supporting documents,
 * with origin/status/naming notes. (Section 4.1 - Milestone 5)
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PackageIndexDto {

    private Long caseId;
    private Long packageId;
    private String profileCode;
    private String profileDisplayName;
    private String generatedAt;

    @Builder.Default
    private List<PackageIndexFormDto> forms = new ArrayList<>();

    @Builder.Default
    private List<PackageIndexDocumentDto> documents = new ArrayList<>();

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PackageIndexFormDto {
        private int sortOrder;
        private String formCode;
        private String displayName;
        private boolean required;
        private boolean provided;      // a current draft exists
        private String origin;         // GENERATED, UPLOADED (null if not provided)
        private String status;         // draft status (null if not provided)
        private String fileName;       // artifact file name (null if not provided)
        private Long draftId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PackageIndexDocumentDto {
        private int sortOrder;
        private String documentCategory;
        private String documentType;
        private boolean required;
        private boolean present;       // at least one matching uploaded document
        private int matchedCount;
        private String namingPattern;
        private String translationRule;
        private String certifiedCopyRule;
    }
}
