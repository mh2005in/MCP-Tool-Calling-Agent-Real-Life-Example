package com.immiauto.dto.forms;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Top-level mapping preview for a case + package profile: the canonical
 * snapshot plus per-form mapped field previews.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MappingPreviewDto {

    private UUID caseId;
    private UUID packageProfileId;
    private String profileCode;
    private String profileDisplayName;
    private String generatedAt; // ISO-8601 timestamp

    private CanonicalDataSnapshotDto snapshot;

    @Builder.Default
    private List<FormMappingPreviewDto> forms = new ArrayList<>();
}
